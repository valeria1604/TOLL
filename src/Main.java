import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {

        long lc1 = 0xFF13A6174CL;
        long lc2 = 0x284CA617FFFFL;
        int partitionSize = 8; // partition-size (p-bit)

        //STAGE 1: PARTITIONING
        writeTitleOfStage("STAGE 1: PARTITIONING");

        //1.1 partitioning
        writeTitleOfStage("1.1 Partitioning");

        ArrayList<String> coefficientsLC1String = new ArrayList<>();
        ArrayList<String> coefficientsLC2String = new ArrayList<>();

        partitioning(lc1, coefficientsLC1String, partitionSize);
        System.out.println("lc1 = " + coefficientsLC1String);

        partitioning(lc2, coefficientsLC2String, partitionSize);
        System.out.println("lc1 = " + coefficientsLC2String);

        System.out.println();

        //1.2 coefficients (C-set) and shifting set (S-set)
        writeTitleOfStage("coefficients (C-set) and shifting set (S-set)");
        HashSet<Integer> coefficients = new HashSet<>();

        makeCoefficientsSet(coefficientsLC1String, coefficients);
        makeCoefficientsSet(coefficientsLC2String, coefficients);
        System.out.println("C-set = " + coefficients);

        //making shifting set (S-set)
        ArrayList<Integer> shiftSetLC1 = new ArrayList<>();
        ArrayList<Integer> shiftSetLC2 = new ArrayList<>();

        makeShiftSet(partitionSize, coefficientsLC1String, shiftSetLC1);
        System.out.println("S-set (lc1) = " + shiftSetLC1);
        makeShiftSet(partitionSize, coefficientsLC2String, shiftSetLC2);
        System.out.println("S-set (lc2) " + shiftSetLC2);

        System.out.println();

        //transform coefficients from (1.1 partitioning) from String to Integer
        writeTitleOfStage("transform coefficients from (1.1 partitioning) from String to Integer");
        ArrayList<Integer> coefficientsLC1 = new ArrayList<>();
        ArrayList<Integer> coefficientsLC2 = new ArrayList<>();

        transformFromStringToInteger(coefficientsLC1String, coefficientsLC1);
        System.out.println("lc1 = " + coefficientsLC1);
        transformFromStringToInteger(coefficientsLC2String, coefficientsLC2);
        coefficientsLC2.remove(coefficientsLC2.size() - 1);
        System.out.println("lc2 = " + coefficientsLC2);

        System.out.println();

        //1.3 linear equations
        writeTitleOfStage("1.3 linear equations");
        System.out.print("lc1 = ");
        printLinearEquations(shiftSetLC1, coefficientsLC1);
        System.out.print("lc2 = ");
        printLinearEquations(shiftSetLC2, coefficientsLC2);

        System.out.println();

        //STAGE 3: REALIZATION OF LINEAR EQUATIONS
        writeTitleOfStage("STAGE 3: REALIZATION OF LINEAR EQUATIONS");

        //3.1 common subexpressions
        writeTitleOfStage("3.1 common subexpressions");
        ArrayList<ArrayList<Integer>> commonSubexpressions = new ArrayList<>();

        findCommonSubexpressions(shiftSetLC1, shiftSetLC2, coefficientsLC1, coefficientsLC2, commonSubexpressions);
        printCommonSubExpressions(commonSubexpressions);
        System.out.println();

        //3.2 final linear equations
        writeTitleOfStage("3.2 final linear equations");
        ArrayList<ArrayList<String>> finalLinearEquationsLC1 = new ArrayList<>();
        ArrayList<ArrayList<String>> finalLinearEquationsLC2 = new ArrayList<>();

        makeFinalLinearEquations(coefficientsLC1, commonSubexpressions, shiftSetLC1, finalLinearEquationsLC1);
        System.out.print("lc1 = ");
        printFinalLinearEquations(finalLinearEquationsLC1);
        //System.out.println(finalLinearEquationsLC1);
        makeFinalLinearEquations(coefficientsLC2, commonSubexpressions, shiftSetLC2, finalLinearEquationsLC2);
        System.out.print("lc2 = ");
        printFinalLinearEquations(finalLinearEquationsLC2);
        //System.out.println(finalLinearEquations2);
        System.out.println();

        //3.3 realization of linear equations
        writeTitleOfStage("3.3 realization of linear equations");
        ArrayList<ArrayList<String>> subexpressionsInRealizedFinalEquations = new ArrayList<>();

        System.out.println("lc1:");
        realizeFinalLinearEquations(finalLinearEquationsLC1, subexpressionsInRealizedFinalEquations);

        System.out.println();

        System.out.println("lc2:");
        realizeFinalLinearEquations(finalLinearEquationsLC2, subexpressionsInRealizedFinalEquations);
    }

    private static void writeTitleOfStage(String title) {
        System.out.println(title);
    }

    private static ArrayList<String> partitioning(long constant, ArrayList<String> arrayList, int partitionSize) {
        long mask = (1L << partitionSize) - 1;

        int index = 0;
        while (constant > 0) {
            long coefficient = constant & mask; //операция AND
            if (coefficient == 0) {
                // игнорируем нулевой коэффициент
            } else if (coefficient == mask) {
                if (index != 0 && arrayList.get(index - 1).contains("seqf")) {
                    arrayList.set(index - 1, "null");
                    arrayList.add("seqf" + partitionSize * 2);
                } else {
                    arrayList.add("seqf" + partitionSize);
                }
            } else {
                arrayList.add(Long.toHexString(coefficient));
            }
            constant >>= partitionSize; //перетягиваем вправо
            index++;
        }
        Collections.reverse(arrayList);
        return arrayList;
    }

    private static void makeCoefficientsSet(ArrayList<String> coefficientsLC, HashSet<Integer> coefficients) {
        for (String str : coefficientsLC) {
            if (!str.contains("seqf") && !str.contains("null")) {
                if (str.matches("^[0-9A-Fa-f]+$")) {
                    try {
                        int num = Integer.parseInt(str, 16);
                        coefficients.add(num);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static void makeShiftSet(int partitionSize, ArrayList<String> coefficientsLC, ArrayList<Integer> shiftSetLC) {
        int shift = 0;
        for (int i = coefficientsLC.size() - 1; i >= 0; i--) {
            if (!coefficientsLC.get(i).contains("null")) {
                shiftSetLC.add(shift);
            }
            shift += partitionSize;
        }
        shiftSetLC.remove(0);
        Collections.reverse(shiftSetLC);
    }

    private static void transformFromStringToInteger(ArrayList<String> coefficientsLCString, ArrayList<Integer> coefficientsLC) {
        for (String coefficient : coefficientsLCString) {
            if (coefficient.contains("seqf") || coefficient.contains("null")) {
                coefficientsLC.add(0);
            } else {
                coefficientsLC.add(Integer.parseInt(coefficient, 16));
            }
        }
    }

    private static void printLinearEquations(ArrayList<Integer> shiftSetLC, ArrayList<Integer> coefficientsLC) {
        for (int i = 0; i < coefficientsLC.size(); i++) {
            if (coefficientsLC.get(i) == 0) {
                System.out.print("seqf");
            } else {
                System.out.print("c" + coefficientsLC.get(i));
            }
            if (i != coefficientsLC.size() - 1) {
                System.out.print("<<");
                System.out.print(shiftSetLC.get(i));
                System.out.print("+");
            }
        }
        System.out.println();
    }

    private static void findCommonSubexpressions(ArrayList<Integer> shiftSetLC1, ArrayList<Integer> shiftSetLC2, ArrayList<Integer> coefficientsLC1, ArrayList<Integer> coefficientsLC2, ArrayList<ArrayList<Integer>> commonSubexpressions) {
        for (int i = 0; i < coefficientsLC1.size() - 1; i++) {
            for (int j = 0; j < coefficientsLC2.size() - 1; j++) {
                if (coefficientsLC1.get(i) == coefficientsLC1.get(j)) {
                    if (coefficientsLC1.get(i + 1) == coefficientsLC2.get(j + 1)) {
                        ArrayList<Integer> newSubExpression = new ArrayList<>();
                        newSubExpression.add(coefficientsLC1.get(i));
                        newSubExpression.add((Math.max(shiftSetLC1.get(i + 1), shiftSetLC2.get(i + 1)) - Math.min(shiftSetLC1.get(i + 1), shiftSetLC2.get(i + 1))));
                        newSubExpression.add(coefficientsLC1.get(i + 1));
                        commonSubexpressions.add(newSubExpression);
                    }
                }
            }
        }
    }

    private static void printCommonSubExpressions(ArrayList<ArrayList<Integer>> commonSubexpressions) {
        for (int i = 0; i < commonSubexpressions.size(); i++) {
            System.out.print("exp" + i + " = ");
            System.out.print("c" + commonSubexpressions.get(i).get(0));
            System.out.print("<<");
            System.out.print(commonSubexpressions.get(i).get(1));
            System.out.print("+");
            System.out.print(commonSubexpressions.get(i).get(2));
        }
        System.out.println();
    }

    private static void makeFinalLinearEquations(ArrayList<Integer> coefficientsLC, ArrayList<ArrayList<Integer>> commonSubexpressions, ArrayList<Integer> shiftSetLC, ArrayList<ArrayList<String>> finalLinearEquationsLC) {
        for (int i = 0; i < coefficientsLC.size() - 1; i++) {
            for (int j = 0; j < commonSubexpressions.size(); j++) {
                ArrayList<String> shiftWithCoefficient = new ArrayList<>();
                if (Objects.equals(coefficientsLC.get(i), commonSubexpressions.get(j).get(0)) && Objects.equals(coefficientsLC.get(i + 1), commonSubexpressions.get(j).get(2))) {
                    shiftWithCoefficient.add("exp" + j);
                    i++;
                    shiftWithCoefficient.add(String.valueOf(shiftSetLC.get(i)));
                    coefficientsLC.remove(i);

                } else {
                    if (coefficientsLC.get(i) == 0) {
                        shiftWithCoefficient.add("seqf");
                    } else {
                        shiftWithCoefficient.add(String.valueOf(coefficientsLC.get(i)));
                    }
                    shiftWithCoefficient.add(String.valueOf(shiftSetLC.get(i)));
                }
                finalLinearEquationsLC.add(shiftWithCoefficient);
            }
        }
        ArrayList<String> shiftWithCoefficient = new ArrayList<>();
        if (coefficientsLC.get(coefficientsLC.size() - 1) == 0) {
            shiftWithCoefficient.add("seqf");
        } else {
            shiftWithCoefficient.add(String.valueOf(coefficientsLC.get(coefficientsLC.size() - 1)));
        }
        finalLinearEquationsLC.add(shiftWithCoefficient);
    }

    private static void printFinalLinearEquations(ArrayList<ArrayList<String>> finalLinearEquations) {
        for (int i = 0; i < finalLinearEquations.size(); i++) {
            if (!(finalLinearEquations.get(i).get(0).contains("seqf") || finalLinearEquations.get(i).get(0).contains("exp"))) {
                System.out.print("c");
            }
            System.out.print(finalLinearEquations.get(i).get(0));
            if (i != finalLinearEquations.size() - 1) {
                System.out.print("<<");
                System.out.print(finalLinearEquations.get(i).get(1));
                System.out.print("+");
            }
        }
        System.out.println();
    }

    private static void realizeFinalLinearEquations(ArrayList<ArrayList<String>> finalLinearEquations, ArrayList<ArrayList<String>> realizedFinalEquations) {
        ArrayList<String> newSubExpression = new ArrayList<>();
        for (int i = finalLinearEquations.size() - 1; i > 1; i--) {
            System.out.print("lc = ");
            newSubExpression.add(finalLinearEquations.get(i - 1).get(0));
            newSubExpression.add(finalLinearEquations.get(i - 1).get(1));
            newSubExpression.add(finalLinearEquations.get(i).get(0));
            realizedFinalEquations.add(newSubExpression);
            finalLinearEquations.remove(i);
            finalLinearEquations.remove(i - 1);
            ArrayList<String> newElement = new ArrayList<>();
            newElement.add("exp" + realizedFinalEquations.size());
            finalLinearEquations.add(newElement);
            printRealizedLinearEquations(finalLinearEquations);
            System.out.println();
        }
    }

    private static void printRealizedLinearEquations(ArrayList<ArrayList<String>> finalLinearEquations) {
        for (int i = 0; i < finalLinearEquations.size(); i++) {
            if (!(finalLinearEquations.get(i).get(0).contains("seqf") || finalLinearEquations.get(i).get(0).contains("exp"))) {
                System.out.print("c");
            }
            System.out.print(finalLinearEquations.get(i).get(0));
            if (i != finalLinearEquations.size() - 1) {
                System.out.print("<<");
                System.out.print(finalLinearEquations.get(i).get(1));
                System.out.print("+");
            }
        }
    }
}
