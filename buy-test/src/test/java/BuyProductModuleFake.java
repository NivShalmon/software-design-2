import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import il.ac.technion.cs.sd.buy.app.BuyProductInitializer;
import il.ac.technion.cs.sd.buy.app.BuyProductInitializerImpl;
import il.ac.technion.cs.sd.buy.app.BuyProductReader;
import il.ac.technion.cs.sd.buy.app.BuyProductReaderImpl;
import il.ac.technion.cs.sd.buy.ext.FutureLineStorageFactory;
import il.ac.technion.cs.sd.buy.test.BuyProductModule;
import library.LibraryModule;

// This module is in the testing project, so that it could easily bind all dependencies from all levels.
public class BuyProductModuleFake extends AbstractModule {
    @Override
    protected void configure() {
        install(new BuyProductModule());
        bind(FutureLineStorageFactory.class).toProvider(FakeFactoryProvider.class);
    }
}
@Singleton
class FakeFactoryProvider implements Provider<FutureLineStorageFactoryFake>{
    static FutureLineStorageFactoryFake ret = new FutureLineStorageFactoryFake();
    @Override
    public FutureLineStorageFactoryFake get() {
        return ret;
    }
}
