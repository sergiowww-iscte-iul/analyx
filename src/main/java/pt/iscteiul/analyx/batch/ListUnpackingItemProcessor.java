package pt.iscteiul.analyx.batch;

import org.springframework.batch.core.annotation.AfterProcess;
import org.springframework.batch.item.ItemProcessor;

import java.util.Iterator;
import java.util.List;

class ListUnpackingItemProcessor<T> implements ItemProcessor<List<T>, T> {

	private Iterator<T> delegate;

	@Override
	public T process(List<T> items) {
		delegate = items.iterator();
		return delegate.hasNext() ? delegate.next() : null;
	}

	@AfterProcess
	public T process() {
		return delegate != null && delegate.hasNext() ? delegate.next() : null;
	}
}
