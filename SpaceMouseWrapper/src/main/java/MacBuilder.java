import com.badlogic.gdx.jnigen.AntScriptGenerator;
import com.badlogic.gdx.jnigen.BuildConfig;
import com.badlogic.gdx.jnigen.BuildExecutor;
import com.badlogic.gdx.jnigen.BuildTarget;
import com.badlogic.gdx.jnigen.NativeCodeGenerator;
import com.rm.spacemousewrapper.SpaceMouse;

public class MacBuilder {

    public static void main(String[] args) throws Exception {

        String libName = "3Dx";

        String[] classes = new String[]{
                "**/*.java",
        };

        String[] excludeClasses = new String[]{
                "**/MacBuilder.java"
        };

        new NativeCodeGenerator().generate(
                "src/main/java/",
                "build/classes/java/main",
                "jni/cpp/jnigen",
                classes,
                excludeClasses
        );

        String[] headerDirs = new String[]{
                "cpp/jnigen",
                "/Library/Frameworks/3DconnexionClient.framework/Headers"
        };

        String[] cIncludes = new String[]{
                "**/*.c"
        };
        String[] cppIncludes = new String[]{
                "**/*.cpp"
        };

        String[] excludes = new String[]{
        };

        String cFlags = "";
        String cppFlags = "";

        BuildConfig buildConfig = new BuildConfig(libName, "../build/tmp/3Dx/target", "libs", "jni");
        buildConfig.sharedLibs = new String[]{
                "../build/classes/java/main"
        };

        BuildTarget mac64 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.MacOsX, true, false);
        mac64.cCompiler = "ccache_clang"; // I used this trick : https://github.com/libgdx/libgdx/wiki/jnigen#ccache
        mac64.cppCompiler = "ccache_clang++"; // I used this trick : https://github.com/libgdx/libgdx/wiki/jnigen#ccache
        mac64.cFlags += cFlags;
        mac64.cppFlags += cppFlags;
        mac64.headerDirs = headerDirs;
        mac64.cIncludes = cIncludes;
        mac64.cppIncludes = cppIncludes;
        mac64.cppExcludes = excludes;
        mac64.libraries = "-framework 3DConnexionClient -F /Library/Frameworks";

        new AntScriptGenerator().generate(buildConfig, mac64);

        boolean macAntExecutionStatus = BuildExecutor.executeAnt("jni/build-macosx64.xml", "-v", "-Drelease=true", "clean", "postcompile");
        if (!macAntExecutionStatus) {
            throw new RuntimeException("Failure to execute mac ant.");
        }

        boolean antExecutionStatus = BuildExecutor.executeAnt("jni/build.xml", "-v", "pack-natives");
        if (!antExecutionStatus) {
            throw new RuntimeException("Failure to execute ant.");
        }
    }

}
