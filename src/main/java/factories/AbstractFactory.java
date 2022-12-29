package factories;

public interface AbstractFactory<T, F> {
   T create(F t);
}
