import org.gsc.Entity.Application;
import org.gsc.config.Args;

/**
 * @Auther: kay
 * @Date: 12/11/18 16:17
 * @Description:
 */
public class Start {

    public static void main(String[] args) {
        Args cfg = Args.getInstance(args);

        Application application = new Application(cfg);

        application.init();
        application.start();

    }
}
