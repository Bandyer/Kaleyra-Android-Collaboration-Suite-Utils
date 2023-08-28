import os

os.chdir("../")
print("|---> GENERATING THIRD PARTIES LICENCES FILE ...")

os.system("./gradlew generateLicenseReport")
os.system("cp collaboration-suite-utils/build/licenses/licenses.json THIRD_PARTY_LICENSES.txt")

print("|---> GENERATED THIRD PARTIES LICENCES FILE to THIRD_PARTY_LICENSES.txt")
