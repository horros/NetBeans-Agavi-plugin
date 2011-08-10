/*
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

/**
 *
 * @author mle
 */
public class Route implements ActiveEditorDrop {

	private String routeName = "";
	private String routePattern = "";
	private String source = "";
	private String output_type = "";
	private String locale = "";
	private boolean cut = false;
	private boolean stop = false;
	private boolean imply = false;
	private String module = "";
	private String action = "";
	private String[] ignores = {};
	private HashMap<String, String> defaults = new HashMap<String, String>();
	private String[] callbacks = {};

	public Route() {
	}

	private String createBody() {
		StringBuilder buffer = new StringBuilder();

		buffer.append("<route");

		if (routeName.length() > 0) {
			buffer.append(" name=\"").append(routeName).append("\"");
		}
		if (routePattern.length() > 0) {
			buffer.append(" pattern=\"").append(routePattern).append("\"");
		}

		if (module.length() > 0) {
			buffer.append(" module=\"").append(module).append("\"");
		}

		if (action.length() > 0) {
			buffer.append(" action=\"").append(action).append("\"");
		}

		if (source.length() > 0) {
			buffer.append(" soruce=\"").append(source).append("\"");
		}

		if (output_type.length() > 0) {
			buffer.append(" output_type=\"").append(output_type).append("\"");
		}

		if (locale.length() > 0) {
			buffer.append(" locale=\"").append(locale).append("\"");
		}

		if (cut == true) {
			buffer.append(" cut=\"true\"");
		}

		if (stop == true) {
			buffer.append(" stop=\"true\"");
		}

		if (imply == true) {
			buffer.append(" imply=\"true\"");
		}

		buffer.append(">");

		if (callbacks.length > 0) {
			buffer.append("\n\t<callbacks>");
			for (String callback : callbacks) {
				buffer.append("\n\t\t<callback class=\"").append(callback).append("\"/>");
			}
			buffer.append("\n\t</callbacks>\n");
		}

		if (ignores.length > 0) {
			buffer.append("\n\t<ignores>");
			for (String ignore : ignores) {
				buffer.append("\n\t\t<ignore>").append(ignore).append("</ignore>");
			}
			buffer.append("\n\t</ignores>\n");
		}

		if (defaults.size() > 0) {
			buffer.append("\n\t<defaults>");
			for (String key : defaults.keySet()) {
				buffer.append("\n\t\t<default for=\"").append(key).append("\">").append(defaults.get(key)).append("</default>");
			}
			buffer.append("\n\t</defaults>\n");


		}

		buffer.append("</route>");

		return buffer.toString();
	}

	@Override
	public boolean handleTransfer(JTextComponent targetComponent) {

		RouteCustomizer c = new RouteCustomizer(this, targetComponent);

		try {

			Document d = targetComponent.getDocument();

			String text = d.getText(0, d.getLength());

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			org.w3c.dom.Document doc = builder.parse(new ByteArrayInputStream(text.getBytes("UTF-8")));

			NodeList elementsByTagNameNS = doc.getElementsByTagNameNS("http://agavi.org/agavi/config/parts/routing/1.0", "routes");

			if (elementsByTagNameNS.getLength() == 0) {

				String confirmText = "The target document does not seem\n "
								+ "to be an Agavi route configuration\n "
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

	void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	void setRoutePattern(String routePattern) {
		this.routePattern = routePattern;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @param output_type the output_type to set
	 */
	public void setOutputType(String output_type) {
		this.output_type = output_type;
	}

	/**
	 * @param locale the locale to set
	 */
	public void setLocale(String locale) {
		this.locale = locale;
	}

	/**
	 * @param callbackClass the callbackClass to set
	 */
	public void setCallbacks(String callbacks) {
		if (callbacks.length() > 0) {
			this.callbacks = callbacks.split("\n");
		}

	}

	/**
	 * @param cut the cut to set
	 */
	public void setCut(boolean cut) {
		this.cut = cut;
	}

	/**
	 * @param stop the stop to set
	 */
	public void setStop(boolean stop) {
		this.stop = stop;
	}

	/**
	 * @param imply the imply to set
	 */
	public void setImply(boolean imply) {
		this.imply = imply;
	}

	void setModule(String module) {
		this.module = module;
	}

	void setAction(String action) {
		this.action = action;
	}

	void setIgnores(String ignores) {
		if (ignores.length() > 0) {
			this.ignores = ignores.split("\n");
		}
	}

	void setDefaults(String defaults) {

		this.defaults.clear();

		if (defaults.length() > 0) {

			for (String def : defaults.split("\n")) {
				String[] item = def.split("=");
				if (item.length == 2) {
					this.defaults.put(item[0].trim(), item[1].trim());
				}
			}

		}

	}
}
