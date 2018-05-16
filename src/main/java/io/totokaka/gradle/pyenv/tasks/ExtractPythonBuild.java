package io.totokaka.gradle.pyenv.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.FileCopyDetails;
import org.gradle.api.file.FileTree;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.regex.Pattern;

@CacheableTask
public class ExtractPythonBuild extends DefaultTask {

    private final Property<Object> target;

    public ExtractPythonBuild() {
        this.target = getProject().getObjects().property(Object.class);
    }

    @TaskAction
    void extractPythonBuild() {
        FileTree pyenvZip = getProject().zipTree(selectMax(
                getProject().getConfigurations().getAt("pyenv").resolve()));
        getProject().copy(spec -> {
            spec.from(pyenvZip);
            spec.eachFile(ExtractPythonBuild::excludeAndRewriteFiles);
            spec.into(target.get());
        });
    }

    @OutputDirectory
    public Property<Object> getTarget() {
        return target;
    }

    /**
     * Exclude files that are irrelevant for python-build, and rewrite the
     * paths, so python-build ends up in the root directory
     *
     * @param details The file to operate on
     */
    private static void excludeAndRewriteFiles(FileCopyDetails details) {
        final Pattern stripPyenvDirPattern = Pattern.compile(
                "^pyenv-[0-9.]+/");
        final Pattern relevantFilePattern = Pattern.compile(
                "^plugins/python-build/(?:share/.*|bin/python-build)");
        final Pattern stripPluginsDirPattern = Pattern.compile(
                "^plugins/python-build/");

        // Strip the pyenv-v{version}/ prefix from the files
        details.setPath(
                stripPyenvDirPattern.matcher(details.getPath())
                        .replaceFirst(""));

        if (!relevantFilePattern.matcher(details.getPath()).matches()) {
            // Exclude irrelevant files
            details.exclude();
        } else {
            // Strip the plugins/python-build/ prefix from the files
            details.setPath(
                    stripPluginsDirPattern.matcher(details.getPath())
                            .replaceFirst(""));
        }
    }

    static File selectMax(Set<File> files) {
        if (files.isEmpty()) {
            throw new IllegalStateException(
                    "Did not find any pyenv dependencies");
        }
        return Collections.max(files, Comparator.comparing(File::getName));
    }
}
