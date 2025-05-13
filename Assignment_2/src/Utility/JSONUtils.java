package Utility;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONUtils {
    // 将 Message 对象转换为 JSON 字符串
    public static String toJson(Message msg) {
        JSONObject json = new JSONObject();
        json.put("type", msg.getType());
        json.put("username", msg.getUsername());
        json.put("shape", msg.getShape());
        json.put("x1", msg.getX1());
        json.put("y1", msg.getY1());
        json.put("x2", msg.getX2());
        json.put("y2", msg.getY2());
        json.put("color", msg.getColor());
        json.put("message", msg.getMessage());
        return json.toJSONString();
    }

    // 将 JSON 字符串解析为 Message 对象
    public static Message fromJson(String jsonStr) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(jsonStr);
        Message msg = new Message();
        msg.setType((String) json.get("type"));
        msg.setUsername((String) json.get("username"));
        msg.setShape(((Long) json.get("shape")).intValue());
        msg.setX1(((Long) json.get("x1")).intValue());
        msg.setY1(((Long) json.get("y1")).intValue());
        msg.setX2(((Long) json.get("x2")).intValue());
        msg.setY2(((Long) json.get("y2")).intValue());
        msg.setColor(((Long) json.get("color")).intValue());
        msg.setMessage((String) json.get("message"));
        return msg;
    }
}