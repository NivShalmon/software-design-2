package il.ac.technion.cs.sd.buy.test;

import com.google.inject.AbstractModule;

import il.ac.technion.cs.sd.buy.ext.FutureLineStorage;
import il.ac.technion.cs.sd.buy.ext.FutureLineStorageFactory;

public class TestLineStorageModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(FutureLineStorageFactory.class).toInstance(new TestStorerFactory());
		bind(FutureLineStorage.class).to(TestStorer.class);
	}

}
