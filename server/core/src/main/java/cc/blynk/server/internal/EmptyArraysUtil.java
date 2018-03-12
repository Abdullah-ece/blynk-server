package cc.blynk.server.internal;

import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.auth.App;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.Tag;
import cc.blynk.server.core.model.web.product.EventReceiver;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.events.Event;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.outputs.graph.GraphDataStream;
import cc.blynk.server.core.model.widgets.ui.tiles.Tile;
import cc.blynk.server.core.model.widgets.ui.tiles.TileTemplate;
import cc.blynk.server.core.model.widgets.web.WebSource;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 30.09.17.
 */
public final class EmptyArraysUtil {

    private EmptyArraysUtil() {
    }

    public static final int[] EMPTY_INTS = {};
    public static final WebSource[] EMPTY_WEB_SOURCES = {};
    public static final DashBoard[] EMPTY_DASHBOARDS = {};
    public static final Tag[] EMPTY_TAGS = {};
    public static final Device[] EMPTY_DEVICES = {};
    public static final Widget[] EMPTY_WIDGETS = {};
    public static final TileTemplate[] EMPTY_TEMPLATES = {};
    public static final Tile[] EMPTY_DEVICE_TILES = {};
    public static final byte[] EMPTY_BYTES = {};
    public static final App[] EMPTY_APPS = {};
    public static final MetaField[] EMPTY_META_FIELDS = {};
    public static final Product[] EMPTY_PRODUCTS = {};
    public static final Event[] EMPTY_EVENTS = {};
    public static final DataStream[] EMPTY_DATA_STREAMS = {};
    public static final EventReceiver[] EMPTY_RECEIVERS = {};
    public static final GraphDataStream[] EMPTY_GRAPH_DATA_STREAMS = {};

}
