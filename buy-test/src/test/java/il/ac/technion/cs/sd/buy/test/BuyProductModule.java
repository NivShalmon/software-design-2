package il.ac.technion.cs.sd.buy.test;

import com.google.inject.AbstractModule;

import il.ac.technion.cs.sd.buy.app.BuyProductInitializer;
import il.ac.technion.cs.sd.buy.app.BuyProductInitializerImpl;
import il.ac.technion.cs.sd.buy.app.BuyProductReader;
import il.ac.technion.cs.sd.buy.app.BuyProductReaderImpl;
import library.Dict;
import library.DictImpl;

// This module is in the testing project, so that it could easily bind all dependencies from all levels.
public class BuyProductModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(BuyProductInitializer.class).to(BuyProductInitializerImpl.class);
		bind(BuyProductReader.class).to(BuyProductReaderImpl.class);
		bind(Dict.class).to(DictImpl.class);
		//TODO: finish
	}
}
