package cc.minetale.slime.loot.predicate;

import cc.minetale.slime.loot.context.LootContext;
import cc.minetale.slime.loot.predicate.util.PredicateLocation;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
@Getter @Setter
public class LocationCheckPredicate extends LootPredicate {
    private Integer offsetX;
    private Integer offsetY;
    private Integer offsetZ;
    private PredicateLocation predicate;

    @JsonCreator
    protected LocationCheckPredicate(int offsetX, int offsetY, int offsetZ,
                                     PredicateLocation predicate) {

        super(PredicateType.LOCATION_CHECK);
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.predicate = predicate;
    }

    @Override
    public boolean test(LootContext ctx) {
        var pos = ctx.getPos();
        if(pos == null) { return false; }

        var toCheck = pos.add(this.offsetX, this.offsetY, this.offsetZ);

        var instance = ctx.getInstance();
        if(instance == null) { return false; }
        return this.predicate.test(instance, toCheck);
    }
}
