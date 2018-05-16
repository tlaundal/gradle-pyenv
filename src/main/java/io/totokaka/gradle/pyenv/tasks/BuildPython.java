package io.totokaka.gradle.pyenv.tasks;

import io.totokaka.gradle.pyenv.DirectoryChecksumUtil;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;

import java.io.*;
import java.util.Arrays;

@CacheableTask
public class BuildPython extends DefaultTask {

    private static final String[] IGNORES = new String[] {"__pycache__"};

    private final Property<Object> pythonBuildDir;
    private final Property<Object> python;
    private final Property<Object> target;

    public BuildPython() {
        this.pythonBuildDir = getProject().getObjects().property(Object.class);
        this.python = getProject().getObjects().property(Object.class);
        this.target = getProject().getObjects().property(Object.class);
    }

    @TaskAction
    void runTask() {
        File target = getProject().file(this.target.get());
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
        String buildDir = getProject().file(pythonBuildDir.get()).getAbsolutePath();
        String target = getProject().file(this.target.get()).getAbsolutePath();
        this.getProject().exec(spec -> {
           spec.setExecutable(buildDir + "/bin/python-build");
           spec.setArgs(Arrays.asList(python.get(), target));
        });
    }

    /**
     * Gets the directory python-build is found in.
     *
     * This is marked as @{@link Internal} because the directory is not
     * directly linked to the python build it produces.
     *
     * @return The {@link Property} for the file python-build should
     *         be found in
     */
    @Internal
    public Property<Object> getPythonBuildDir() {
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
    public Property<Object> getPython() {
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
    public Property<Object> getTarget() {
        return target;
    }
}
