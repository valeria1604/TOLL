import java.util.HashSet;

public class Main {
    public static void main(String[] args) {

        long lc1 = 0xFF13A6174CL;
        long lc2 = 0x284CA617FFFFL;

        int partitionSize = 8; // размер разбиения (p-bit)

        System.out.println("First phase: Partitioning");
        HashSet<Long> coefficients = partitioning(lc1, lc2, partitionSize);

    }

    private static HashSet<Long> partitioning(long lc1, long lc2, int partitionSize) {
        //для визуализации этапа partioning
        System.out.println("Partitioning");
        visualizePartioning(lc1, partitionSize);
        visualizePartioning(lc2, partitionSize);
        System.out.println();

        // Разбиваем константы на коэффициенты и сохраняем их в множество C (coefficients)
        System.out.println("Coefficients");
        HashSet<Long> cSet = new HashSet<>();
        generateCoefficientsSet(lc1, cSet, partitionSize);
        generateCoefficientsSet(lc2, cSet, partitionSize);
        System.out.println("C = "+ cSet);
        System.out.println();


        //пусть будет, если не понадобится удалю sSet, seqfSet
        //множество S для хранения сдвигов коэффициентов
        HashSet<Integer> sSet = new HashSet<>();
        //множество Seqf для хранения последовательностей subexpression
        HashSet<String> seqfSet = new HashSet<>();

        System.out.println("Linear Equations: ");
        String equation1 = vizualizeLinearEquation(lc1, sSet, seqfSet, partitionSize);
        System.out.println("Linear Equation 1: " + equation1);
        String equation2 = vizualizeLinearEquation(lc2, sSet, seqfSet, partitionSize);
        System.out.println("Linear Equation 2: " + equation2);

        return cSet;
    }

    // Метод для разбиения большой константы на коэффициенты и сохранения их в множестве C
    private static void generateCoefficientsSet(long constant, HashSet<Long> coefficients, int partitionSize) {
        //maska dlugoscia partionSize, w ktorej tylko jedynki
        long mask = (1L << partitionSize) - 1;

        while (constant > 0) {
            long coefficient = constant & mask; //операция AND
            if (coefficient != 0 && coefficient != mask) {
                coefficients.add(coefficient);
            }
            constant >>= partitionSize; //перетягиваем вправо
        }
    }

    private static void visualizePartioning(long constant, int partitionSize) {
        long mask = (1L << partitionSize) - 1;

        StringBuilder line = new StringBuilder();
        line.append("lc = ");
        StringBuilder reversedValuesLine = new StringBuilder();

        while (constant > 0) {
            long coefficient = constant & mask;
            if (coefficient == 0) {
                // игнорируем нулевой коэффициент
            } else if (coefficient == mask) {
                reversedValuesLine.append("seqf(" + partitionSize + ")");
            } else {
                reversedValuesLine.append(Long.toHexString(coefficient));
            }
            reversedValuesLine.append(" | ");
            constant >>= partitionSize;
        }

        // чтобы всё было в обратном порядке
        String[] values = reversedValuesLine.toString().split("\\s*\\|\\s*");
        reversedValuesLine.setLength(0); //
        for (int i = values.length - 1; i >= 0; i--) {
            reversedValuesLine.append(values[i]);
            if (i != 0) {
                reversedValuesLine.append(" | ");
            }
        }

        line.append(reversedValuesLine);
        System.out.println(line);
    }

    // Метод для формирования линейного уравнения на основе коэффициентов, сдвигов и subexpression
    private static String vizualizeLinearEquation(long constant, HashSet<Integer> sSet, HashSet<String> seqfSet, int partitionSize) {
        StringBuilder equation = new StringBuilder();
        long mask = (1L << partitionSize) - 1;
        int shift = 0;

        while (constant > 0) {
            long coefficient = constant & mask;

            if (coefficient == 0) {
                // Игнорируем нулевой коэффициент
            } else if (coefficient == mask) {
                String seqf = "seqf(" + partitionSize + ")";
                equation.insert(0, seqf + "<<");
                seqfSet.add(seqf);
            } else {
                equation.insert(0, shift+"+");
                equation.insert(0, "c" + coefficient + "<<");
            }

            sSet.add(shift);
            shift += partitionSize;
            constant >>= partitionSize;
        }

        // Удаляем последний символ "+" в уравнении
        if (equation.length() > 0) {
            equation.deleteCharAt(equation.length() - 1);
        }

        return equation.toString();
    }
}
