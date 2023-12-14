import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.remoteParameters.hashiCorpVaultParameter
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2023.11"

project {

    vcsRoot(HttpsGithubComJetBrainsTeamcityAwsLambdaPlugin)

    buildType(Build)
}

object Build : BuildType({
    name = "Build"
    description = "AWS CLI is installed"

    params {
        hashiCorpVaultParameter {
            name = "env.AWS_SECRET_ACCESS_KEY"
            query = "aws/data/access!/AWS_SECRET_ACCESS_KEY"
        }
        hashiCorpVaultParameter {
            name = "env.AWS_ACCESS_KEY_ID"
            query = "aws/data/access!/AWS_ACCESS_KEY_ID"
        }
        param("env.AWS_DEFAULT_REGION", "eu-west-1")
    }

    vcs {
        root(HttpsGithubComJetBrainsTeamcityAwsLambdaPlugin)
    }

    steps {
        script {
            name = "Generate and upload"
            id = "Connect_to_AWS"
            scriptContent = "./upload_images.sh ${'$'}AWS_DEFAULT_REGION gradle %build.number%"
        }
    }
})

object HttpsGithubComJetBrainsTeamcityAwsLambdaPlugin : GitVcsRoot({
    name = "https://github.com/JetBrains/teamcity-aws-lambda-plugin"
    url = "https://github.com/JetBrains/teamcity-aws-lambda-plugin"
    branch = "refs/heads/master"
})
