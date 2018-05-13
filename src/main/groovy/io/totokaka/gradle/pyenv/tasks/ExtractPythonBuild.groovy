package io.totokaka.gradle.pyenv.tasks

import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCopyDetails
import org.gradle.api.file.FileTree
import org.gradle.api.provider.Property
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

import java.util.regex.Pattern

/**
 * Gradle task that extracts python-build from a pyenv dependency
 */
class ExtractPythonBuild extends DefaultTask {

    static final Pattern stripPyenvDirPattern = Pattern.compile($/^pyenv-[0-9.]+//$)
    static final Pattern relevantFilePattern = Pattern.compile($/^plugins/python-build/(?:share/.*|bin/python-build)/$)
    static final Pattern stripPluginsDirPattern = Pattern.compile($/^plugins/python-build//$)

    @OutputDirectory
    Property<File> target

    /**
     * Exclude files that are irrelevant for python-build, and rewrite the paths, so python-build ends up in the root
     * directory
     * @param details The file to operate on
     */
    static void excludeAndRewriteFiles(FileCopyDetails details) {
        // Strip the pyenv-v{version}/ prefix from the files
        details.path = stripPyenvDirPattern.matcher(details.path).replaceFirst('')

        if (!relevantFilePattern.matcher(details.path).matches()) {
            // Exclude irrelevant files
            details.exclude()
        } else {
            // Strip the plugins/python-build/ prefix from the files
            details.path = stripPluginsDirPattern.matcher(details.path).replaceFirst('')
        }
    }

    @TaskAction
    void extractPythonBuild() {
        FileTree pyenv = project.zipTree(selectPyenvFile(project.configurations.pyenv.resolve()))
        project.copy({ spec ->
            spec.from(pyenv)
            spec.eachFile(this.&excludeAndRewriteFiles as Action)
            spec.into(task)
        } as Action)
    }

    static File selectPyenvFile(Set<File> files) {
        return files.max({ a , b -> (a.name <=> b.name) })
    }

}
