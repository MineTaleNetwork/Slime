package cc.minetale.slime.map;

import lombok.Getter;
import lombok.Setter;
import net.minestom.server.coordinate.Vec;
import org.bson.Document;

import java.util.List;

//TODO Make functional
public class Zone {

    @Getter @Setter private Vec firstPos;
    @Getter @Setter private Vec secondPos;

    public Zone(Document document) {
        var pos = document.getList("firstPos", Double.class);
        this.firstPos = new Vec(pos.get(0), pos.get(1), pos.get(2));

        pos = document.getList("secondPos", Double.class);
        this.secondPos = new Vec(pos.get(0), pos.get(1), pos.get(2));
    }

    public Document toDocument() {
        return new Document()
                .append("firstPos", List.of(this.firstPos.x(), this.firstPos.y(), this.firstPos.z()))
                .append("secondPos", List.of(this.secondPos.x(), this.secondPos.y(), this.secondPos.z()));
    }

}
