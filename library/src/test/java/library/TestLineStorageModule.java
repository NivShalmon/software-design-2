package library;

import com.google.inject.AbstractModule;

import il.ac.technion.cs.sd.buy.ext.FutureLineStorage;
import il.ac.technion.cs.sd.buy.ext.FutureLineStorageFactory;
import library.TestStorerFactory;
import library.DictFactory.DictFactoryImpl;

public class TestLineStorageModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(FutureLineStorageFactory.class).to(TestStorerFactory.class);
		bind(FutureLineStorage.class).to(TestStorer.class);
		bind(DictFactory.class).to(DictFactoryImpl.class);
	}

}
