/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package php.agavi;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpProgram.InvalidPhpProgramException;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.api.util.UiUtils;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import php.agavi.ui.options.AgaviOptions;

/**
 * The main workhorse. Handles opening and handling input and output of the
 * Agavi batch file / shell script.
 * 
 * @author Tomas Mysik
 * @author Markus Lervik
 */
public class AgaviScript {

    public static final String SCRIPT_NAME = "agavi"; // NOI18N
    public static final String SCRIPT_NAME_LONG = SCRIPT_NAME + FileUtils.getScriptExtension(true);
    public static final String OPTIONS_SUB_PATH = "Agavi"; // NOI18N
    public static final String CMD_INIT_PROJECT = "project-create"; // NOI18N
    public static final String CMD_CLEAR_CACHE = "project-cache-remove"; // NOI18N

    /**
     * Attempt to find files named "agavi.bat" or "agavi" on the user's PATH.
     * Commented out code to locate the main Agavi PHP build script, because
     * we are currently doing all the work via the shell script.
     * 
     * @return a list of locations where the agavi scripts were found
     */
    public static List<String> detectAgaviScript() {

        List<String> files = FileUtils.findFileOnUsersPath("agavi.bat", "agavi");
        return files;

    }

    /**
     * Attempt to detect the Agavi version. Even though it's running regexps on every
     * line of two files, it's guaranteed to be faster than starting the Agavi
     * process and parsing the "Version: n.n.n" -string from the output
     * 
     * @param script the location of the Agavi batch file / shell script
     * @return the version detected, or "Cannot detect Agavi version" if no version found
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static String detectAgaviVersion(String script) throws FileNotFoundException, IOException {

        File f = new File(script);

        if (f != null && f.isFile()) {

            BufferedReader reader = new BufferedReader(new FileReader(f));
            Pattern sourcePattern = Pattern.compile(".*? AGAVI_SOURCE_DIRECTORY=\"(.*?)\"");
            Pattern majorVersion  = Pattern.compile("\\s*AgaviConfig::set\\('agavi.major_version', '([0-9])'\\);");
            Pattern minorVersion  = Pattern.compile("\\s*AgaviConfig::set\\('agavi.minor_version', '([0-9])'\\);");
            Pattern microVersion  = Pattern.compile("\\s*AgaviConfig::set\\('agavi.micro_version', '([0-9])'\\);");
            String line;
            StringBuilder sb = new StringBuilder();
            String sep = System.getProperty("file.separator");
            StringBuilder versionResultString = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                Matcher m = sourcePattern.matcher(line);
                if (m.matches()) {
                    sb.append(m.group(1)).append(sep).append("version.php");
                    File version = new File(sb.toString());
                    BufferedReader versionReader = new BufferedReader(new FileReader(version));
                    String versionString;
                    Matcher majorMatcher;
                    Matcher minorMatcher;
                    Matcher microMatcher;
                    while ((versionString = versionReader.readLine()) != null) {
                        majorMatcher = majorVersion.matcher(versionString);
                        if (majorMatcher.matches()) {
                            versionResultString.append(majorMatcher.group(1)).append(".");
                            continue;
                        }
                        minorMatcher = minorVersion.matcher(versionString);
                        if (minorMatcher.matches()) {
                            versionResultString.append(minorMatcher.group(1)).append(".");
                            continue;
                        }
                        microMatcher = microVersion.matcher(versionString);
                        if (microMatcher.matches()) {
                            versionResultString.append(microMatcher.group(1));
                            return versionResultString.toString();
                        }
                    }
                    
                    
                }
                
                
            }
            
            return "Cannot detect Agavi version";

        }


        return null;

    }
    private String projectPath;
    private String projectName;
    private int lineNum;
    private String pName;
    private int actionNum = 0;

    AgaviScript(String script) {
    }

    /**
     * Get the default, <b>valid only</b> Agavi script.
     * @return the default, <b>valid only</b> Agavi script.
     * @throws InvalidPhpProgramException if Agavi script is not valid.
     */
    public static AgaviScript getDefault() throws InvalidPhpProgramException {
        String agavi = AgaviOptions.getInstance().getAgavi();
        System.out.println("Agavi path: " + agavi);
        if (agavi.equals("")) {
            throw new InvalidPhpProgramException("Invalid Agavi script");
        }
        return new AgaviScript(agavi);
    }

    /**
     * @return full IDE options Agavi path
     */
    public static String getOptionsPath() {
        return UiUtils.OPTIONS_PATH + "/" + getOptionsSubPath(); // NOI18N
    }

    /**
     * @return IDE options Agavi subpath
     */
    public static String getOptionsSubPath() {
        return OPTIONS_SUB_PATH;
    }

    /**
     * Remove spaces from a string
     * @param s input string
     * @return a string without spaces
     */
    public String removeSpaces(String s) {
        StringTokenizer st = new StringTokenizer(s, " ", false);
        StringBuilder sb = new StringBuilder();
        while (st.hasMoreElements()) {
            sb.append(st.nextElement());
        }
        return sb.toString();
    }

    /**
     * Initialize a new Agavi project
     * 
     * @param phpModule the current PHP module
     * @param params [not used]
     * @return true if we successfully created a project, false otherwise
     * @throws IOException if there is a problem using the Agavi script
     * @throws InterruptedException if the Agavi script execution is interrupted before it finishes
     */
    public boolean initProject(PhpModule phpModule, String[] params) throws IOException, InterruptedException {

        this.projectName = phpModule.getDisplayName();
        this.projectPath = phpModule.getSourceDirectory().getPath();

        pName = removeSpaces(projectName);
        pName = Character.toUpperCase(pName.charAt(0)) + pName.toLowerCase().substring(1);
        Process agavi = null;
        PrintWriter stdin = null;
        InputStream stdout = null;

        ProcessBuilder pb = new ProcessBuilder(AgaviOptions.getInstance().getAgavi(), "project-create");
        pb.redirectErrorStream(true);
        try {
            agavi = pb.start();
            stdin = new PrintWriter(new BufferedWriter(new OutputStreamWriter(agavi.getOutputStream())));
            stdout = agavi.getInputStream();
            String line;
            lineNum = 0;
            StringBuilder buffer = new StringBuilder();
            int ch;

            // Read the output from the Agavi script until it's finished
            while ((ch = stdout.read()) > -1) {

                // We don't care about carriage returns
                if ((char) ch == '\r') {
                    continue;
                }

                // If we recieve a new line, it means the script has printed
                // a whole line, and process that. If not, check if there are
                // still data in the buffer. If there isn't, we can assume that
                // the Agavi script is waiting for user input, so process the line
                // and clear the current string buffer.
                if ((char) ch == '\n' || stdout.available() == 0) {
                    line = buffer.toString();
                    buffer = new StringBuilder();
                    processProjectCreateLines(line, stdin);
                    continue;
                }
                // There's data in the output buffer and we don't have a new line,
                // so process the char.
                buffer.append((char) ch);


            }
            agavi.waitFor();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);

        } finally {
            if (stdin != null) {
                stdin.close();
            }
            if (stdout != null) {
                stdout.close();
            }
            if (agavi != null) {
                agavi.destroy();
            }
        }

        return AgaviPhpFrameworkProvider.getInstance().isInPhpModule(phpModule);
    }

    /**
     * Merge two arrays into a third array
     * 
     * @return a merged array of the same type as the input arrays
     */
    static <T> T[] mergeArrays(T[]... arrays) {
        List<T> list = new LinkedList<T>();
        for (T[] array : arrays) {
            list.addAll(Arrays.asList(array));
        }
        @SuppressWarnings("unchecked")
        T[] merged = (T[]) Array.newInstance(arrays[0].getClass().getComponentType(), list.size());
        return list.toArray(merged);
    }

    /**
     * Process the lines we recieve from the project-create target
     * 
     * @param line a single line from the Agavi script
     * @param stdin A print writer connected to the Agavi script's stdin
     */
    private void processProjectCreateLines(String line, PrintWriter stdin) {

        if (line.startsWith("Project base directory")) {
            stdin.print(projectPath);
            stdin.println();
            stdin.flush();
        }
        if (line.startsWith("Project name [New Agavi Project]:")) {
            stdin.print(projectName);
            stdin.println();
            stdin.flush();
        }
        if (line.startsWith("Project prefix (used, for example, in the project base action)")) {
            stdin.print(pName);
            stdin.println();
            stdin.flush();
        }
        if (line.startsWith("Default template extension")) {
            stdin.println();
            stdin.flush();
        }


    }

    /**
     * Launch the module-wizard target
     * 
     * @param project The current Agavi project
     * @param moduleName The module to create
     * @param actions The actions to create for the module
     * @param actionViews A map mapping views to actions
     * @param progressBar the progress bar to show at the bottom of the IDE
     * @return true if success, false on error
     * @throws InterruptedException if the Agavi script is interrupted before it finishes
     * @throws IOException if there's a problem opening or using the Agavi script
     */
    public boolean moduleWizardCommand(File projectDirectory, String moduleName, String[] actions, Map<String, String[]> actionViews, ProgressHandle progressBar) throws InterruptedException, IOException {

        boolean retval = false;
        Process agavi = null;
        PrintWriter stdin = null;
        InputStream stdout = null;
        ProcessBuilder pb = new ProcessBuilder(AgaviOptions.getInstance().getAgavi(), "module-wizard");
        pb.directory(projectDirectory);
        pb.redirectErrorStream(true);
        try {
            agavi = pb.start();
            stdin = new PrintWriter(new BufferedWriter(new OutputStreamWriter(agavi.getOutputStream())));
            stdout = agavi.getInputStream();
            String line;
            lineNum = 0;
            StringBuilder buffer = new StringBuilder();
            int ch;
            while ((ch = stdout.read()) > -1) {

                if ((char) ch == '\r') {
                    continue;
                }
                // We got either an EOL or there's no more stuff in the buffer
                // but the buffer didn't return -1 (finished), which means
                // we've got a line of output, or the script is waiting for
                // input
                if ((char) ch == '\n' || stdout.available() == 0) {
                    line = buffer.toString();
                    //System.out.println(line);
                    buffer = new StringBuilder();
                    processModuleWizardLines(line, stdin, moduleName, actions, actionViews, progressBar);

                    // XXX Need to dissect the Agavi build system and rewrite it in Java.
                    // There's no way of knowing if something went horribly
                    // wrong when running the script, eg. it's waiting for input
                    // for something we did not expect. In that case we won't get
                    // -1 from the buffer, and the buffer WILL be empty, but that will
                    // also happen if we're waiting for the build script to generate
                    // files (we are reading data faster than the script will output it).

                    continue;
                }

                buffer.append((char) ch);


            }
            agavi.waitFor();
            retval = true;
        } catch (IOException ex) {
            retval = false;
            Exceptions.printStackTrace(ex);

        } finally {
            if (stdin != null) {
                stdin.close();
            }
            if (stdout != null) {
                stdout.close();
            }
            if (agavi != null) {
                agavi.destroy();
            }

        }

        return retval;


    }

    /**
     * Process the lines we get from the module-wizard -target
     * 
     * @param line a single line from the Agavi script
     * @param stdin a PrintWriter connected to the Agavi script's stdin
     * @param moduleName the name of the module to create
     * @param actions an array of actions to create
     * @param actionViews a map mapping the views to actions
     * @param progress the progress bar being shown at the bottom of the IDE
     * @return true on success, false on failure
     */
    private boolean processModuleWizardLines(String line, PrintWriter stdin, String moduleName, String[] actions, Map<String, String[]> actionViews, ProgressHandle progress) {

        if (line.startsWith("Module name:")) {
            this.actionNum++;
            stdin.print(moduleName);
            stdin.println();
            stdin.flush();
            progress.progress(actionNum);
            return true;
        }


        if (line.startsWith("Space-separated list of actions to create for")) {
            StringBuilder sb = new StringBuilder();
            for (String action : actions) {
                sb.append(action).append(" ");
            }
            this.actionNum++;
            stdin.print(sb.toString());
            stdin.println();
            stdin.flush();
            progress.setDisplayName("Creating actions...");
            progress.progress(actionNum);
            return true;
        }


        if (line.startsWith("Space-separated list of views to create for")) {
            for (String action : actions) {
                if (line.startsWith("Space-separated list of views to create for " + action)) {
                    StringBuilder sb = new StringBuilder();
                    String[] views = actionViews.get(action);
                    for (String view : views) {
                        this.actionNum++;
                        sb.append(view).append(" ");
                    }
                    progress.setDisplayName("Creating views for \"" + action + "\"");
                    progress.progress(actionNum);
                    stdin.print(sb.toString());
                    stdin.println();
                    stdin.flush();
                    return true;
                }
            }
        }

        return false;


    }
}
