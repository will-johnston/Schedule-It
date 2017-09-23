import subprocess
import os
import shutil
import  datetime

gitLocation = "/home/staging/git/Schedule-It"
buildloglocation = "/home/staging/builds/build.log"
buildslocation = "/home/staging/builds/"

# git pull changes
gitpullraw = subprocess.Popen(["git", "pull"], cwd=gitLocation, stdout=subprocess.PIPE)
gitpullout = gitpullraw.stdout.readline()

# if gitpullout == "Already up-to-date." + '\n':
#     print "No new builds"
#     quit()
# else:
#     print ""
# get last commit
lograw = subprocess.Popen(["git", "--no-pager", "log"], cwd=gitLocation, stdout=subprocess.PIPE)
loglines = lograw.stdout.readlines()
lastlogcommit = ""
for line in loglines:
    if "commit" in line and '\t' not in line:
        # get commit numbers that aren't in comments
        splitLine = line.split(" ")
        lastlogcommit = splitLine[1].strip()
        print "Using commit: " + lastlogcommit
        break

# load build log
doBuild = False
buildnumber = -1
try:
    BuildLogLines = open(buildloglocation, mode='r').readlines()
    lastbuildcommit = ""
    lastbuildnumber = -1
    lastsuccess = False
    for line in BuildLogLines:
        if line == "":
            continue
        if "Commit" in line:
            splitLine = line.split(":")
            lastbuildcommit = splitLine[1].strip()
        elif "Success" in line:
            splitLine = line.split(":")
            print "converting {0} into bool {1}".format(splitLine[1], splitLine[1].strip())
            lastsuccess = bool(splitLine[1].strip())
        elif "Build" in line:
            splitLine = line.split(":")
            lastbuildnumber = int(splitLine[1].strip())

        if lastbuildcommit is not "" and lastbuildnumber is not "":
            break

    #check if build
    if lastbuildcommit != lastlogcommit:
        print "build commit {0}, log commit {1}".format(lastlogcommit, lastlogcommit)
        doBuild = True
    elif lastsuccess is False:
        print "last success is False"
        doBuild = True
    if doBuild:
        buildnumber = lastbuildnumber + 1

except Exception:
    doBuild = True
    print "Couldn't parse build log, so building anyway"
    buildnumber = 1
    # log probably doesn't exist, create, and leave empty
# if change, build
if not doBuild:
    print "No need for a build"
    quit()
# make directories
buildPath = buildslocation + str(buildnumber) +'/'
os.mkdir(buildPath)
# make html first, it's just copying
shutil.copytree(gitLocation + "/app/", buildPath + "app/")
print "Copied html into /app"
# make api server, generate ant, and call
print "Copying {0} to {1}".format(gitLocation + "/build.xml", buildPath + "build.xml")
shutil.copy(gitLocation + "/build.xml", buildPath + "build.xml")
shutil.copytree(gitLocation + "/backend/", buildPath + "backend/")
antcall = subprocess.Popen(["ant"], cwd=buildPath, stdout=subprocess.PIPE);
antresultlines = antcall.stdout.readlines();
antresult = ""
buildSuccess = False
for line in antresultlines:
    antresult = antresult + line + '\n'
    if "BUILD" in line:
        lineSplit = line.split(" ")
        if lineSplit[1].strip() == "SUCCESSFUL":
            buildSuccess = True
        else:
            buildSuccess = False
print antresult
# if build fails, log fail
# log success
BuildLogLines = ""
try:
    BuildLogLines = open(buildloglocation, mode='r').read()
except Exception:
    BuildLogLines = ""
BuildLogWrite = open(buildloglocation, mode='w')
newlines = "Build: {0}\nDate: {1}\nSuccess: {2}\nCommit: {3}\n".format(buildnumber,                                                                   datetime.date.today().strftime("%B %d, %Y"),
                                                                          buildSuccess, lastlogcommit)
BuildLogWrite.write(newlines + BuildLogLines)
# cleanup
