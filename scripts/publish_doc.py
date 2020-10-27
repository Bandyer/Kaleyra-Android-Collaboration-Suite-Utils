#!/usr/bin/python
import os
import re
import shutil
import sys
import subprocess

api_key = ""
app_id = ""
configs = []

sdk_company = sys.argv[1]
sdk_module = sys.argv[2]
sdk_version = sys.argv[3]

sdk_releasing_package = sdk_company + ":" + sdk_module + ":" + sdk_version

implementation_gradle_SdkPattern = re.compile(r"(%s:%s:\d+.\d+.\d+)+" % (sdk_company, sdk_module),
                                              re.IGNORECASE)

print "|---> RELEASING API DOC " + sdk_releasing_package

# ------------------------------------------------------------------------------
def copy_and_overwrite(from_path, to_path):
    if os.path.exists(to_path):
        shutil.rmtree(to_path)
    try:
        shutil.copytree(from_path, to_path)
    except:
       print("Path not exiting. Removing folder")

#################################################################################
########################## CHECK MASTER CHANGES ################################
#################################################################################
os.chdir("../")
os.system("git checkout master")

hasChangesOnMaster = subprocess.check_output(
    "if [ \"$(git status -s | wc -l)\" -gt \"0\" ]; then echo \"true\"; else echo \"false\"; fi",
    shell=True).strip()
if hasChangesOnMaster == "true":
    sys.exit(
        "You have not committed some files on the master branch, to proceed commit manually the changes")

#################################################################################
####################### CHECK MASTER COMPILES CORRECTLY #########################
#################################################################################
builded = os.system("./gradlew clean assembleRelease")
if builded != 0:
    sys.exit("Project is not compiling correctly")

#################################################################################
########################## RELEASE API DOC ######################################
#################################################################################
# generate documentation of the lib
os.system("./gradlew clean dokkaDoc")

# update github documentation
branchExists = os.system("git checkout gh-pages")
os.system("git pull")

if branchExists != 0:
    createBranch = os.system("git branch -f gh-pages github/gh-pages")
    if createBranch != 0:
        sys.exit("gp-pages branch did not exist and the creation of a new branch failed.")

copy_and_overwrite("%s/build/kDoc/" % sdk_module, "kDoc/")
copy_and_overwrite("%s/build/jDoc/" % sdk_module, "jDoc/")

# commit documentation
os.system("git add kDoc/ jDoc/")

os.system("git commit -m 'release v%s'" % sys.argv[3])
os.system("git push github")

##################################################################################
##################### RESET STATUS QUO ###########################################
##################################################################################
os.system("git checkout develop")
# clean generated files
os.system("./gradlew clean")
