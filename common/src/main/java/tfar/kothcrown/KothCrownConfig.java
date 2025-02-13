package tfar.kothcrown;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class KothCrownConfig {

    public static final KothCrownConfig SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;

  //  public static final Client CLIENT;
 //   public static final ForgeConfigSpec CLIENT_SPEC;




    static {
        final Pair<KothCrownConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(KothCrownConfig::new);
        SERVER_SPEC = specPair.getRight();
        SERVER = specPair.getLeft();

      //  final Pair<Client, ForgeConfigSpec> specPair2 = new ForgeConfigSpec.Builder().configure(Client::new);
      //  CLIENT_SPEC = specPair2.getRight();
       // CLIENT = specPair2.getLeft();
    }

    public final ForgeConfigSpec.DoubleValue taxRate;

    public KothCrownConfig(ForgeConfigSpec.Builder builder) {
        builder.push("general");
        taxRate  = builder.defineInRange("tax_rate",.125,0,1);
        builder.pop();
    }

}
