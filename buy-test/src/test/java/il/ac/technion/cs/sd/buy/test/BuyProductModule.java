package il.ac.technion.cs.sd.buy.test;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Named;

import il.ac.technion.cs.sd.buy.app.BuyProductInitializer;
import il.ac.technion.cs.sd.buy.app.BuyProductInitializerImpl;
import il.ac.technion.cs.sd.buy.app.BuyProductReader;
import il.ac.technion.cs.sd.buy.app.BuyProductReaderImpl;
import library.Dict;
import library.DictImpl;
import library.DictFactory;
import library.DoubleKeyDict;
import library.DoubleKeyDictImpl;
import library.LibraryMoudle;
import library.DoubleKeyDictFactory;

// This module is in the testing project, so that it could easily bind all dependencies from all levels.
public class BuyProductModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(BuyProductInitializer.class).to(BuyProductInitializerImpl.class);
		bind(BuyProductReader.class).to(BuyProductReaderImpl.class);
		install(new LibraryMoudle());
	}
	
	@Provides
	@Named("orderIdToOrder")
	public Dict orderIdToOrderProvider(DictFactory f){
		return f.create("orderIdToOrder");
	}
	
	@Provides
	@Named("userIdToOrderIds")
	public Dict userIdToOrderIdsProvider(DictFactory f){
		return f.create("userIdToOrderIds");
	}
	
	@Provides
	@Named("productIdToOrderIds")
	public Dict productIdToOrderIdsProvider(DictFactory f){
		return f.create("productIdToOrderIds");
	}
	
	@Provides
	@Named("orderIdToHistory")
	public Dict orderIdToHistoryProvider(DictFactory f){
		return f.create("orderIdToHistory");
	}
	
	@Provides
	@Named("userProductAmount")
	public DoubleKeyDict userProductAmountProvider(DoubleKeyDictFactory f){
		return f.create("userProductAmount");
	}
}
