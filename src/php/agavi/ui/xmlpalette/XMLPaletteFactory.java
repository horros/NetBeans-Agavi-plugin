/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package php.agavi.ui.xmlpalette;

import java.io.IOException;
import javax.swing.Action;
import org.netbeans.spi.palette.DragAndDropHandler;
import org.netbeans.spi.palette.PaletteActions;
import org.netbeans.spi.palette.PaletteController;
import org.netbeans.spi.palette.PaletteFactory;
import org.openide.util.Lookup;
import org.openide.util.datatransfer.ExTransferable;

/**
 *
 * @author mle
 */
public class XMLPaletteFactory {

    public static final String XML_PALETTE_FOLDER = "XMLPalette";
    private static PaletteController palette = null;

    public static PaletteController createPalette() throws IOException {

        if (palette == null) {
            palette = PaletteFactory.createPalette(XML_PALETTE_FOLDER, new XMLPaletteActions(), null, new XMLDragAndDropHandler());
        }

        return palette;
    }

    private static class XMLDragAndDropHandler extends DragAndDropHandler {

        public XMLDragAndDropHandler() {
            super(true);
        }

        @Override
        public void customize(ExTransferable et, Lookup lkp) {
        }
    }


    private static class XMLPaletteActions extends PaletteActions {

        //Add new buttons to the Palette Manager here:
        public Action[] getImportActions() {
            return null;
        }

        //Add new contextual menu items to the palette here:
        public Action[] getCustomPaletteActions() {
            return null;
        }

        //Add new contextual menu items to the categories here:
        public Action[] getCustomCategoryActions(Lookup arg0) {
            return null;
        }

        //Add new contextual menu items to the items here:
        public Action[] getCustomItemActions(Lookup arg0) {
            return null;
        }

        //Define the default action here:
        public Action getPreferredAction(Lookup arg0) {
            return null;
        }
    }
}
