import sys, getopt
import subprocess
import shutil
import os

def checkRoot():
    return os.getuid() == 0

def printhelp():
    print "Usage deployer.py -b <build number>"
    print "If no arguments, prints out the list of available builds"
    print "Flags"
    print "-b\t\tDeploys a build file"
    print "-z\t\tZips a build file"


def stopapache():
    subprocess.Popen(["service", "apache2", "stop"])

def startapache():
    subprocess.Popen(["service", "apache2" , "start"])

def stopapiserver():
    # kill all instances of java (I know it's not very good)
    pidsPipe = subprocess.Popen(["pidof", "java"], stdout=subprocess.PIPE)
    pipdslist = pidsPipe.stdout.read().split(" ")
    for pid in pipdslist:
        if pid.strip() == "":
            continue
        print "killing " + pid
        subprocess.Popen(["kill", pid])

def startapiserver():
    #apiLocation = "/home/staging/"
    jarLocation = "/home/scheduleit/backend/classes/ScheduleIt.jar"
    #startPipe = subprocess.Popen(["bash", "spawn.sh"], cwd=apiLocation, stdout=subprocess.PIPE)
    #print startPipe.stdout.read()
    startpipe = subprocess.Popen(["java", "-jar", jarLocation, "&"])
    #print startpipe.stdout.read()

def getBuilds():
    builds = list()  # number, location
    buildsLogLocation = "/home/staging/builds/build.log"
    buildsLocation = "/home/staging/builds/"
    try:
        log = open(buildsLogLocation, 'r')
        loglines = log.readlines()
        build = ""
        success = ""
        for line in loglines:
            if "Build" in line:
                build = line.split(":")[1].strip()
            elif "Success" in line:
                success = line.split(":")[1].strip()
            if build is not "" and success is not "":
                if success == "True":
                    builds.append([build, buildsLocation + build + '/'])
                build = ""
                success = ""
    except IOError:
        print "An IO Error occurred"
        builds = None
    except Exception:
        print "There's no builds yet!"
        builds = None
    return builds

def main():
    buildsLocation = "/home/staging/builds/"
    serverLocation = "/home/scheduleit/app/"
    serverBackupLocation = "/home/scheduleit/app.backup/"
    apiLocation = "/home/scheduleit/backend/"
    apiBackupLocation = "/home/scheduleit/backend.backup/"
    deploynumber = ""
    getZip = ""
    hasopts = False
    printBuilds = False
    try :
        opts, args = getopt.getopt(sys.argv[1:], "hz:b:", ["zipBuild=", "buildFile="])
        # print len(opts)
        if len(opts) > 0:
            hasopts = True
    except Exception:
        # print builds list
        hasopts = False
    if hasopts:
        for opt, arg in opts:
            if opt == '-h':
                printhelp()
                quit()
            elif opt in ("-z", "--zipBuild"):
                getZip = arg
            elif opt in ("-b", "--buildFile"):
                deploynumber = arg
        #print "Deploy number: ", deploynumber
        #print "Zip number: ", getZip
    builds = getBuilds()
    if not hasopts:
        print "Builds: "
        i = 0
        for build in builds:
            if i > 14:
                break
            print "{0}: {1}".format(build[0], build[1])
            i = i + 1
        quit()
    if getZip is not "":
        # zip the build
        # get the build to zip
        build = ""
        for abuild in builds:
            if abuild[0] == deploynumber:
                build = abuild
                break
        if build is "":
            print "Build Number doesn't exist!"
            quit()
        zipPipe =subprocess.Popen(["zip", "-r {0} {1}".format("build" + deploynumber + ".zip", buildsLocation + build + '/')], cwd = buildsLocation, stdout=subprocess.PIPE)
        if "error" in zipPipe.stdout.read():
            print "Failed to zip file"
    if deploynumber is not "":
        build = ""
        for abuild in builds:
            if abuild[0] == deploynumber:
                build = abuild
                break
        if build is "":
            print "Build Number doesn't exist!"
            quit()
        print "Stopping Apache"
        stopapache()
        print "Stopping API Server"
        stopapiserver()
        if os.path.exists(serverBackupLocation):
            print "Removing Server Backup (/app.backup)"
            shutil.rmtree(serverBackupLocation, ignore_errors=True)
        print "Backing up previous Apache directory (/app.backup)"
        shutil.copytree(serverLocation, serverBackupLocation)
        print "Removing Server Directory (/app)"
        if os.path.exists(serverLocation):
            shutil.rmtree(serverLocation, ignore_errors=True)
        print "Installing new Server Directory (/app)"
        shutil.copytree(buildsLocation + deploynumber + "/app/", serverLocation)
        print "App installed"
        if os.path.exists(apiBackupLocation):
            print "Removing API Backup"
            shutil.rmtree(apiBackupLocation, ignore_errors=True)
        print "Backing up previous API directory (/backend.backup)"
        shutil.copytree(apiLocation, apiBackupLocation)
        print "Removing API directory (/backend)"
        if os.path.exists(apiLocation):
            shutil.rmtree(apiLocation, ignore_errors=True)

        print "Installing new API directory (/backend)"
        shutil.copytree(buildsLocation + deploynumber + "/ant/", apiLocation)
        shutil.copy(apiLocation + "jar/ScheduleIt.jar", apiLocation + "classes/ScheduleIt.jar")

        print "API installed"

        print "Starting API Server"
        startapiserver()
        print "Starting Apache"
        startapache()
        print "Deploy finished"

if __name__ == "__main__":
    if checkRoot() == False:
        print "Must run as ROOT"
        quit()
    main()