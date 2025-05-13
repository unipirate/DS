package Utility;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WhiteboardXMLUtils {
    public static void saveToXML(List<WhiteboardShape> shapes, File file) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();

        Element root = doc.createElement("whiteboard");
        doc.appendChild(root);

        for (WhiteboardShape shape : shapes) {
            Element shapeEl = doc.createElement("shape");
            shapeEl.setAttribute("type", shape.getType());
            shapeEl.setAttribute("stroke", String.valueOf(shape.getStrokeWidth()));

            Color c = shape.getColor();
            shapeEl.setAttribute("color", c.getRed() + "," + c.getGreen() + "," + c.getBlue());

            if (shape.getText() != null) {
                shapeEl.setAttribute("text", shape.getText());
            }

            for (Point p : shape.getPoints()) {
                Element pointEl = doc.createElement("point");
                pointEl.setAttribute("x", String.valueOf(p.x));
                pointEl.setAttribute("y", String.valueOf(p.y));
                shapeEl.appendChild(pointEl);
            }

            root.appendChild(shapeEl);
        }

        Transformer tf = TransformerFactory.newInstance().newTransformer();
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        tf.transform(new DOMSource(doc), new StreamResult(file));
    }

    public static List<WhiteboardShape> loadFromXML(File file) throws Exception {
        List<WhiteboardShape> shapes = new ArrayList<>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(file);

        NodeList shapeNodes = doc.getElementsByTagName("shape");
        for (int i = 0; i < shapeNodes.getLength(); i++) {
            Element shapeEl = (Element) shapeNodes.item(i);
            String type = shapeEl.getAttribute("type");
            int stroke = Integer.parseInt(shapeEl.getAttribute("stroke"));
            String[] rgb = shapeEl.getAttribute("color").split(",");
            Color color = new Color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));

            String text = shapeEl.hasAttribute("text") ? shapeEl.getAttribute("text") : null;
            NodeList pointNodes = shapeEl.getElementsByTagName("point");
            List<Point> points = new ArrayList<>();
            for (int j = 0; j < pointNodes.getLength(); j++) {
                Element pointEl = (Element) pointNodes.item(j);
                int x = Integer.parseInt(pointEl.getAttribute("x"));
                int y = Integer.parseInt(pointEl.getAttribute("y"));
                points.add(new Point(x, y));
            }
            WhiteboardShape shape = new WhiteboardShape(type, points, color, stroke, text);
            shapes.add(shape);
        }
        return shapes;
    }
}