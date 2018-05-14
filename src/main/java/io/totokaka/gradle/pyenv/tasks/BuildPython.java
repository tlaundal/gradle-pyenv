package io.totokaka.gradle.pyenv.tasks;

import io.totokaka.gradle.pyenv.DirectoryChecksumUtil;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;

import java.io.*;
import java.util.Arrays;

@CacheableTask
public class BuildPython extends DefaultTask {

    private static final String[] IGNORES = new String[] {"__pycache__"};

    private final Property<File> pythonBuildDir;
    private final Property<String> python;
    private final Property<File> target;

    public BuildPython() {
        this.pythonBuildDir = getProject().getObjects().property(File.class);
        this.python = getProject().getObjects().property(String.class);
        this.target = getProject().getObjects().property(File.class);
    }

    @TaskAction
    void runTask() {
        File target = this.target.get();
        if (DirectoryChecksumUtil.readAndVerifyDirectoryChecksum(
                target, IGNORES)) {
            throw new StopExecutionException(
                    "A valid python build already exists in the target. " +
                            "Skipping");
        }

        this.buildPython();
        DirectoryChecksumUtil.checksumDirectoryAndWrite(target, IGNORES);
    }

    private void buildPython() {
        this.getProject().exec(spec -> {
           spec.setExecutable(pythonBuildDir.get().getAbsolutePath() +
                   "/bin/python-build");
           spec.setArgs(Arrays.asList(python.get(),
                   target.get().getAbsolutePath()));
        });
    }

    /**
     * Gets the directory python-build is found in.
     *
     * This is marked as @{@link Internal} because the directory is not
     * directly linked to the python build it produces.
     *
     * @return The {@link Property} for the {@link File} python-build should
     *         be found in
     */
    @Internal
    public Property<File> getPythonBuildDir() {
        return pythonBuildDir;
    }

    /**
     * The python version that should be built.
     *
     * For example: "3.6.0"
     *
     * @return The {@link Property} for the python version
     */
    @Input
    public Property<String> getPython() {
        return python;
    }

    /**
     * The target folder for the build.
     *
     * This is what later may be used as the python prefix.
     *
     * @return The {@link Property} for the target directory for the build
     */
    @OutputDirectory
    public Property<File> getTarget() {
        return target;
    }
}
