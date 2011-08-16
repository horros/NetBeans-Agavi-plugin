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
package php.agavi.util;

import java.io.File;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.modules.php.api.editor.EditorSupport;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import php.agavi.AgaviPhpFrameworkProvider;
import php.agavi.ui.actions.ListViewsDialog;

/**
 *
 * @author Markus Lervik <markus.lervik@necora.fi>
 */
public final class AgaviUtils {

	public static final String DIR_VIEWS = "views";
	public static final String DIR_ACTIONS = "actions";
	public static final Pattern VIEW_NAME = Pattern.compile("^(\\w+)View");
	public static final Pattern VIEW_FILE = Pattern.compile("^(\\w+)View.class.php");
	public static final Pattern TEMPLATE_FILE = Pattern.compile("^(\\w+).php$");
	public static final Pattern ACTION_FILE = Pattern.compile("^(\\w+)Action.class.php$");
	public static final Pattern ACTION_NAME = Pattern.compile("^(\\w+)Action");
	public static String ACTION_METHOD_PREFIX = "execute";

	/**
	 * Return the action associated with this view. 
	 * 
	 * @param viewFile the current view file open in the editor
	 * @return the action file if found, null otherwise
	 */
	public static FileObject getAction(FileObject viewFile) {


		Matcher viewMatcher = VIEW_FILE.matcher(viewFile.getNameExt());

		PhpModule module = PhpModule.inferPhpModule();

		if (viewMatcher.matches()) {
			
			Preferences preferences = module.getPreferences(AgaviPhpFrameworkProvider.class, true);
			String sourceDir = preferences.get("sourceDir", "");
			File src;
			
			// Prebuild the path to the modules-directory
			String pathToModules;
			
			if (sourceDir.length() > 0) {
				pathToModules = module.getSourceDirectory().getPath() + "/" + sourceDir + "/app/modules/";
			} else {
				pathToModules = module.getSourceDirectory().getPath() + "/app/modules/";
			}

			Pattern p = Pattern.compile(viewMatcher.group(1) + "(.*)Action(.*)\\.class\\.php");


			StringBuilder sb = new StringBuilder();
			EditorSupport editorSupport = Lookup.getDefault().lookup(EditorSupport.class);
			
			for (PhpClass phpClass : editorSupport.getClasses(viewFile)) {
				
				// Make sure that the class we're operating on is a view class
				Matcher matcher = VIEW_NAME.matcher(phpClass.getName());
				if (matcher.matches()) {
					
					// Split the view class into parts so we know the module and the path 
					// to the view class and remove the "View" part from the class name
					String[] split = phpClass.getName().replace("View", "").split("_");
					
					sb.append(split[0]).append("/actions/");
					
					// The class name is the last part of the array
					String name = split[split.length - 1];
					
					if (name.length() > 0) {
						
						// Split the name of the class on uppercase characters
						String[] classParts = name.split("(?<=[a-z])(?=[A-Z])");
						
						// Build the path to the action directory
						for (int i = 1; i < split.length - 1; i++) {
							sb.append(split[i]).append("/");
						}

						src = new File(pathToModules + "/" + sb.toString());

						if (src != null && src.exists()) {

							// Ok, test with the full class name we got, minus the "View"-part (should not
							// work, but you never know
							FileObject f = AgaviPhpFrameworkProvider.locate(FileUtil.toFileObject(src), name + "Action.class.php", false);
							if (f != null) {
								return f;
							} else {
								// Right, we didn't find a file name based on the full view class name,
								// so start picking off parts from the end and see if we get a match
								// that way
								for (int j = classParts.length - 1; j > 0; j--) {
									name = name.replace(classParts[j], "");
									f = AgaviPhpFrameworkProvider.locate(FileUtil.toFileObject(src), name + "Action.class.php", false);
									if (f != null) {
										return f;
									} else {
										continue;
									}
								}
							}
						}
					}
				}
			}
		}

		return null;


	}

	/**
	 * Attempt to determine if this file is a view
	 * 
	 * @param fo the view file
	 * @return  true if the file appears to be a view, false otherwise
	 */
	public static boolean isView(FileObject fo) {

		EditorSupport editorSupport = Lookup.getDefault().lookup(EditorSupport.class);
		for (PhpClass phpClass : editorSupport.getClasses(fo)) {
			if (VIEW_NAME.matcher(phpClass.getName()).matches()
							&& VIEW_FILE.matcher(fo.getNameExt()).matches()) {
				return true;
			}
		}
		return false;

	}

	/**
	 * Attempt to determine if we can find an action for this view
	 * 
	 * @param fo the view file
	 * @return  true if we can find an action, false otherwise
	 */
	public static boolean isViewWithAction(FileObject fo) {
		return isView(fo) && getAction(fo) != null;
	}

	/**
	 * Attempt to determine if we can find a view for this action
	 * 
	 * @param fo the action file
	 * @return true if we can find a view, false otherwise
	 */
	public static boolean isActionWithView(FileObject fo) {
		return isAction(fo) && hasView(fo) != false;
	}

	/**
	 * Return the view associated with this action. Because of how views
	 * are selected in Agavi, the only reasonable way to attempt to figure
	 * out which view to return, is to find all views with the same "base" name
	 * as the action, and provide a popup for the user to select the wanted view from.
	 * 
	 * @param actionFile the action file open in the editor
	 * @return the view file to open
	 */
	public static FileObject getView(FileObject actionFile) {

		if (hasView(actionFile)) {
			return showViewDialog(getViews(actionFile));
		} else {
			return null;
		}


	}

	/**
	 * Attempt to determine if the action has view files
	 * 
	 * @param actionFile the action file open in the editor
	 * @return true if the action has views, false otherwise
	 */
	public static boolean hasView(FileObject actionFile) {
		List files = getViews(actionFile);
		return (files != null && files.size() > 0);
	}

	/**
	 * Get a list of view files associated with this action
	 * 
	 * @param actionFile the action file open in the editor
	 * @return a list of views associated with this action or null if no views found
	 */
	private static List<FileObject> getViews(FileObject actionFile) {

		Matcher actionMatcher = ACTION_FILE.matcher(actionFile.getNameExt());

		if (actionMatcher.matches()) {

			FileObject parent = actionFile.getParent();

			PhpModule module = PhpModule.inferPhpModule();

			Pattern p = Pattern.compile(actionMatcher.group(1) + "(.*)View(.*)\\.class\\.php");

			EditorSupport editorSupport = Lookup.getDefault().lookup(EditorSupport.class);

			StringBuilder sb = new StringBuilder();

			String path = "";
			for (PhpClass phpClass : editorSupport.getClasses(actionFile)) {
				Matcher actionClassNameMatcher = ACTION_NAME.matcher(phpClass.getName());
				if (actionClassNameMatcher.matches()) {
					String[] split = phpClass.getName().replace("Action", "").split("_");
					sb.append(split[0]).append("/views/");
					for (int i = 1; i < split.length - 1; i++) {
						sb.append(split[i]).append("/");
					}
					path = sb.toString();
				}
			}


			List<FileObject> list = null;


			Preferences preferences = module.getPreferences(AgaviPhpFrameworkProvider.class, true);
			String sourceDir = preferences.get("sourceDir", "");
			File src;

			if (sourceDir.length() > 0) {
				src = new File(module.getSourceDirectory().getPath() + "/" + sourceDir + "/app/modules/" + path);
			} else {
				src = new File(module.getSourceDirectory().getPath() + "/app/modules/" + path);
			}
			if (src != null && src.exists()) {
				list = AgaviPhpFrameworkProvider.locate(FileUtil.toFileObject(src), p, false);
			}
			return list;
		}

		return null;
	}

	/**
	 * Show a dialog from which the user can choose which view file to open
	 * 
	 * @param viewList the list of views to display
	 * @return the view file chosen, or null if the user didn't choose a file
	 */
	private static FileObject showViewDialog(List<FileObject> viewList) {
		// If we found views, present a dialog to the user
		if (viewList != null && !viewList.isEmpty()) {
			ListViewsDialog dialog = new ListViewsDialog(null, true, viewList);
			dialog.pack();
			dialog.setLocationRelativeTo(null);
			dialog.setVisible(true);
			return dialog.getSelectedFile();

		} else {
			return null;
		}
	}

	/**
	 * Attempt to determine if the file is an action
	 * 
	 * @param actionFile the action file open in the editor
	 * @return true if it appears to be an action, false otherwise
	 */
	private static boolean isAction(FileObject actionFile) {

		EditorSupport editorSupport = Lookup.getDefault().lookup(EditorSupport.class);
		for (PhpClass phpClass : editorSupport.getClasses(actionFile)) {

			if (ACTION_NAME.matcher(phpClass.getName()).matches()
							&& ACTION_FILE.matcher(actionFile.getNameExt()).matches()) {
				return true;
			}

		}
		return false;

	}
}
