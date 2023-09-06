package org.yunghegel.gdx.gizmo.picker;

public class PickerColorEncoder {

    /**
     * Decodes a rgba8888 color code to a game object id.
     *
     * @param rgba8888Code
     *            rgba8888 color code
     * @return game object id
     */
    public static int decode(int rgba8888Code) {
        int id = (rgba8888Code & 0xFF000000) >>> 24;
        id += ((rgba8888Code & 0x00FF0000) >>> 16) * 256;
        id += ((rgba8888Code & 0x0000FF00) >>> 8) * 256 * 256;

        return id;
    }

    /**
     * Encodes a game object id to a GameObjectIdAttribute with rgb channels.

     * @return the game object id, encoded as rgb values
     */
    public static PickerIDAttribute encodeRaypickColorId(int id) {
        PickerIDAttribute goIDa = new PickerIDAttribute();
        encodeRaypickColorId(id, goIDa);
        return goIDa;
    }

    /**
     * Encodes a id to a GameObjectIdAttribute with rgb channels.
     *
     * @param id
     *            id
     * @param out
     *            encoded id as attribute
     */
    public static void encodeRaypickColorId(int id, PickerIDAttribute out) {
        out.r = id & 0x000000FF;
        out.g = (id & 0x0000FF00) >>> 8;
        out.b = (id & 0x00FF0000) >>> 16;
    }

}
