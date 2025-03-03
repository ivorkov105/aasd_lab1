import java.util.*;
import java.util.function.Consumer;

public class ExpList<E> implements Iterable<E> {

    /**дефолтный размер массива*/
    private static final int DEFAULT_CAPACITY = 10;
    /**инициализация пустых данных*/
    private static final Object[] EMPTY_DATA = {};

    private static final Object[] DEFAULT_CAPACITY_EMPTY_DATA = {};
    /**хранилище для элементов коллекции aka ёмкость*/
    transient Object[] elementData;
    /**размер*/
    private int size;

    public ExpList(int initCapacity) {
        if (initCapacity > 0) {
            this.elementData = new Object[initCapacity];
        } else if (initCapacity == 0) {
            this.elementData = EMPTY_DATA;
        } else {
            throw new IllegalArgumentException("Неправильный размер +" + initCapacity);
        }
    }

    public ExpList() {
        this.elementData = DEFAULT_CAPACITY_EMPTY_DATA;
    }

    public ExpList(Collection<? extends E> collection) {
        Object[] obj = collection.toArray();
        if ((size != obj.length)) {
            elementData = Arrays.copyOf(obj, size, Object[].class);
        }
    }

    public int size() {
        return size;
    }

    public void clone(ExpList<E> expList) {
        this.size = expList.size;
        this.elementData = new Object[expList.elementData.length];
        System.arraycopy(expList.elementData, 0, this.elementData, 0, expList.size);
    }
/**Так как в Java нет деструкторов, то украл, кхм, позаимствовал реализацию из интерфейса AutoClosable*/
    public void close() throws Exception {
        elementData = null;
    }

    public void clear() {
        Arrays.fill(elementData, 0, size, null);
        size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isNull() {
        return elementData != null;
    }

    public boolean isNullOrEmpty() {
        return size == 0 || elementData == null;
    }

    public E get(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Индекс: " + index + ", Размер: " + size);
        }
        return (E) elementData[index];
    }

    public boolean has(Object element) {
        for (int i = 0; i < size; i++) {
            if (element.equals(get(i))) {
                return true;
            }
        }
        return false;
    }

    public E set(int index, E element) {
        Objects.checkIndex(index, size);
        E oldValue = get(index);
        elementData[index] = element;
        return oldValue;
    }

    public int getId(E element) {
        for (int i = 0; i < size; i++) {
            if (get(i).equals(element)) {
                return i;
            }
        }
        return -1;
    }

    public void changeCapacity() {
        if (elementData == DEFAULT_CAPACITY_EMPTY_DATA) {
            elementData = new Object[DEFAULT_CAPACITY];
        } else if (size == elementData.length) {
            ExpList<E> newExpList = new ExpList<>(elementData.length * 2);
            newExpList.clone(this);
            elementData = newExpList.elementData;
        } else if (size == elementData.length / 2 && size > DEFAULT_CAPACITY) {
            ExpList<E> newExpList = new ExpList<>(elementData.length / 2);
            newExpList.clone(this);
            elementData = newExpList.elementData;
        }
    }

    public void add(E element) {
        size++;
        changeCapacity();
        elementData[size] = element;
    }

    public void add(E element, int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Индекс: " + index + ", Размер: " + size);
        }
        changeCapacity();
        System.arraycopy(elementData, index, elementData, index + 1, size - index);
        elementData[index] = element;
        size++;
    }

    public void delete(E element) {
        int newSize = 0;
        for (int i = 0; i < size; i++) {
            if (!elementData[i].equals(element)) {
                elementData[newSize++] = elementData[i];
            }
        }
        Arrays.fill(elementData, newSize, size, null);
        size = newSize;
        changeCapacity();
    }

    public void delete(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Индекс: " + index + ", Размер: " + size);
        }
        elementData[index] = null;
        System.arraycopy(elementData, index + 1, elementData, index, size - index - 1);
        elementData[size--] = null;
        changeCapacity();
    }
    /**не представляю, как реализовать end() на джаве, но его аналогом частично может выступить hasNext(), за тем исключением, что end()
     выставляет маркер на позиции после последнего элемента, а hasNext() определяет, есть ли следующий элемент,
     с rend() - такая же ситуация, но используем reverseIterator()
     * спасибо всемогущей яве, по сути своей весь функционал итераторов идёт уже из коробки и ничего добавлять не надо!!!
     * <3<3<3 это для джавы*/
    @Override
    public Iterator<E> iterator() {
        return new ExpListIterator();
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        Iterable.super.forEach(action);
    }

    @Override
    public Spliterator<E> spliterator() {
        return Iterable.super.spliterator();
    }

    private class ExpListIterator implements Iterator<E> {

        private int curIndex = 0;

        @Override
        public boolean hasNext() {
            return curIndex < size;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return (E) elementData[curIndex++];
        }

        @Override
        public void remove() {
            Iterator.super.remove();
        }

        @Override
        public void forEachRemaining(Consumer<? super E> action) {
            Iterator.super.forEachRemaining(action);
        }
    }

    /**нельзя перегрузить одну и ту же функцию дважды, поэтому эта не перегружена*/

    public Iterator<E> reverseIterator() {
        return new ReverseExpListIterator();
    }

    private class ReverseExpListIterator implements Iterator<E> {
        private int curIndex = size - 1;

        @Override
        public boolean hasNext() {
            return curIndex >= 0;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return (E) elementData[curIndex--];
        }

        @Override
        public void remove() {
            Iterator.super.remove();
        }

        @Override
        public void forEachRemaining(Consumer<? super E> action) {
            Iterator.super.forEachRemaining(action);
        }
    }
}