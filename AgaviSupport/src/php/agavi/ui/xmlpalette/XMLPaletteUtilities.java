package php.agavi.ui.xmlpalette;

import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.modules.editor.indent.api.Indent;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;

/**
 *
 * @author gw152771
 * @author Markus Lervik <markus.lervik@necora.fi>
 */
public class XMLPaletteUtilities {

    /**
     * Insert a string into the document. 
     *
     * @param s the string to insert
     * @param target the target text component
     * @throws BadLocationException
     */
    public static void insert(final String s, final JTextComponent target) throws BadLocationException {

        final StyledDocument doc = (StyledDocument) target.getDocument();
        if (doc == null) {
            return;
        }

        /**
         * Inline class to handle running the insert.
         * This is needes because of NbDocument.runAtomicAsUser
         * which locks the document for the duration of the process.
         */
        class InsertFormattedText implements Runnable {

            public void run() {
                try {
                    insertAndReindent(s, target, doc);
                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }

        InsertFormattedText insert = new InsertFormattedText();
        NbDocument.runAtomicAsUser(doc, insert);

    }

    /**
     * Insert a string into the document and reindent the whole document
     * when done.
     *
     * @param s the string to add
     * @param target the target text component
     * @param doc the document object
     * @return the start-position (position of the caret)
     * 
     * @throws BadLocationException
     */
    private static int insertAndReindent(String s, JTextComponent target, Document doc) throws BadLocationException {

        int start = -1;

        try {

            //Find the location in the editor,
            //and if it is a selection, remove it,
            //to be replaced by the dropped item:
            Caret caret = target.getCaret();
            int p0 = Math.min(caret.getDot(), caret.getMark());
            int p1 = Math.max(caret.getDot(), caret.getMark());
            doc.remove(p0, p1 - p0);

            start = caret.getDot();

            // Insert the string in the document
            // and reindent the whole thing
            doc.insertString(start, s, null);
            Indent indent = Indent.get(doc);
            indent.lock();
            try {
                indent.reindent(0, doc.getLength() - 1);
            } finally {
                indent.unlock();
            }



        } catch (BadLocationException ble) {
            Exceptions.printStackTrace(ble);
        }

        return start;
    }
}
