/*
 * item.java
 *
 * Created on Jun 4, 2007, 12:53:46 PM
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package php.agavi.ui.xmlpalette.items;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.openide.text.ActiveEditorDrop;
import org.w3c.dom.NodeList;
import php.agavi.ui.xmlpalette.XMLPaletteUtilities;

public class Validator implements ActiveEditorDrop {

	private String comment = "";
	private String name = "";
	private String validatorClass = "";
	private String exportName = "";
	private String depends = "";
	private String methodName = "";
	private String provides = "";
	private String translationDomain = "";
	private String source = "";
	private HashMap<String, String> arguments = new HashMap<String, String>();
	private String[] errors = {};
	private HashMap<String, String> parameters = new HashMap<String, String>();
	private String base = "";
	private boolean required = true;
	private String severity = "";

	public Validator() {
	}

	private String createBody() {
		StringBuilder buffer = new StringBuilder();

		buffer.append("\n");
		if (comment.length() > 0) {
			buffer.append("<!-- ").append(comment).append(" -->\n");
		}

		buffer.append("<validator");

		if (name.length() > 0) {
			buffer.append(" name=\"").append(name).append("\"");
		}

		if (validatorClass.length() > 0) {
			if (validatorClass.equalsIgnoreCase("custom")) {
				validatorClass = "##INSERT THE CUSTOM CLASS HERE##";
			}
			buffer.append(" class=\"").append(validatorClass).append("\"");
		}

		if (methodName.length() > 0) {
			buffer.append(" method=\"").append(methodName).append("\"");
		}

		if (depends.length() > 0) {
			buffer.append(" depends=\"").append(depends).append("\"");
		}

		if (provides.length() > 0) {
			buffer.append(" provides=\"").append(provides).append("\"");
		}

		if (exportName.length() > 0) {
			buffer.append(" export=\"").append(exportName).append("\"");
		}

		if (severity.length() > 0 && !severity.equalsIgnoreCase("default")) {
			buffer.append(" severity=\"").append(severity).append("\"");
		}

		if (source.length() > 0) {
			buffer.append(" source=\"").append(source).append("\"");
		}

		if (translationDomain.length() > 0) {
			buffer.append(" translation_domain=\"").append(translationDomain).append("\"");
		}

		if (required == false) {
			buffer.append(" required=\"false\"");
		}

		buffer.append(">");

		if (arguments.size() > 0) {
			buffer.append("\n\t<arguments>");
			for (String key : arguments.keySet()) {
				try {
					Integer.parseInt(key);
					buffer.append("\n\t\t<argument>").append(arguments.get(key)).append("</argument>");
				} catch (NumberFormatException nfe) {
					buffer.append("\n\t\t<argument name=\"").append(key).append("\">").append(arguments.get(key)).append("</argument>");
				}
			}
			buffer.append("\n\t</arguments>");
		}

		if (errors.length > 0) {
			buffer.append("\n\t<errors>");
			for (String error : errors) {
				buffer.append("\n\t\t<error>").append(error).append("</error>");
			}
			buffer.append("\n\t</errors>");
		}

		if (parameters.size() > 0) {
			buffer.append("\n\t<ae:parameters>");
			for (String key : parameters.keySet()) {
				try {
					Integer.parseInt(key);
					buffer.append("\n\t\t<ae:parameter>").append(parameters.get(key)).append("</ae:parameter>");
				} catch (NumberFormatException nfe) {
					buffer.append("\n\t\t<ae:parameter name=\"").append(key).append("\">").append(parameters.get(key)).append("</ae:parameter>");
				}
			}
			buffer.append("\n\t</ae:parameters>");
		}

		buffer.append("\n</validator>");

		return buffer.toString();
	}

	@Override
	public boolean handleTransfer(JTextComponent targetComponent) {

		ValidatorCustomizer c = new ValidatorCustomizer(this, targetComponent);

		try {

			Document d = targetComponent.getDocument();

			String text = d.getText(0, d.getLength());

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			org.w3c.dom.Document doc = builder.parse(new ByteArrayInputStream(text.getBytes("UTF-8")));
			NodeList elementsByTagNameNS = doc.getElementsByTagNameNS("http://agavi.org/agavi/config/parts/validators/1.0", "validators");

			if (elementsByTagNameNS.getLength() == 0) {

				String confirmText = "The target document does not seem\n "
								+ "to be an Agavi validation configuration\n "
								+ "file. Continue?";
				NotApplicableWindow notApplicableWindow = new NotApplicableWindow();

				boolean ok = notApplicableWindow.showDialog(confirmText);

				if (!ok) {
					return false;
				}

			}

		} catch (Exception e) {
		}


		boolean accept = c.showDialog();
		if (accept) {
			String body = createBody();
			try {
				XMLPaletteUtilities.insert(body, targetComponent);
			} catch (BadLocationException ble) {
				accept = false;
			}
		}
		return accept;

	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	void setName(String name) {
		this.name = name;
	}

	void setClass(String validatorClass) {
		this.validatorClass = validatorClass;
	}

	void setExport(String exportName) {
		this.exportName = exportName;
	}

	void setDepends(String depends) {
		this.depends = depends;
	}

	void setMethod(String methodName) {
		this.methodName = methodName;
	}

	void setProvides(String provides) {
		this.provides = provides;
	}

	void setTranslationDomain(String translationDomain) {
		this.translationDomain = translationDomain;
	}

	void setSource(String source) {
		this.source = source;
	}

	void setArguments(String arguments) {

		this.arguments.clear();

		if (arguments.length() > 0) {
			String[] tmp = arguments.split("\n");
			Integer i = 0;
			for (String keyval : tmp) {
				if (keyval.contains("=")) {
					String[] argument = keyval.split("=");
					this.arguments.put(argument[0].trim(), argument[1].trim());
				} else {
					this.arguments.put(i.toString(), keyval);
					i++;
				}
			}
		}

	}

	void setErrors(String errors) {
		if (errors.length() > 0) {
			this.errors = errors.split("\n");
		}
	}

	void setParameters(String parameters) {
		this.parameters.clear();

		if (parameters.length() > 0) {
			String[] tmp = parameters.split("\n");
			Integer i = 0;
			for (String keyval : tmp) {
				if (keyval.contains("=")) {
					String[] argument = keyval.split("=");
					this.parameters.put(argument[0].trim(), argument[1].trim());
				} else {
					this.parameters.put(i.toString(), keyval);
					i++;
				}
			}
		}
	}

	void setBase(String base) {
		this.base = base;
	}

	void setRequired(boolean selected) {
		this.required = selected;
	}

	void setSeverity(String severity) {
		this.severity = severity;
	}
}
