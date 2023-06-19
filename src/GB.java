import java.util.LinkedList;

public class GB {
    public static LinkedList<Integer> BFSearch(LinkedList<Integer> T) {
        LinkedList<Integer> R = new LinkedList<>();
        R.add(1);

//        R = synthesize(R, T).get(0);
//
//        if (synthesize(R, T).get(1).isEmpty()) {
//            return R;
//        } else {
            LinkedList<Integer> Wr = new LinkedList<>(R);
            LinkedList<Integer> Wt = new LinkedList<>(T);
            int n = Wr.size() + Wt.size();
            int bw = determineMaxBitWidth(T);
            while (!Wt.isEmpty()) {
                int m = n;
                LinkedList<Integer> Xr = new LinkedList<>(Wr);
                LinkedList<Integer> Xt = new LinkedList<>(Wt);
                n = 0;
                Wr.clear();
                Wt.clear();

                for (int i = 0; i < m; i++) {

                    for (int j = 1; j < Math.pow(2, bw + 1) - 1; j += 2) {
                        if (!Xr.contains(j) && !Xt.contains(j)) {
                            LinkedList<Integer> jList = new LinkedList<>();
                            jList.add(j);
                            LinkedList<LinkedList<Integer>> AB = synthesize(Xr, jList);

                            if (AB.get(1).isEmpty()) {
                                n = n + 1;
                                LinkedList<Integer> newR = new LinkedList<>(AB.get(0));
                                LinkedList<Integer> newT = new LinkedList<>(Xt);
                                LinkedList<LinkedList<Integer>> WrWt = synthesize(newR, newT);
                                if (WrWt.get(1).isEmpty()) {
                                    return WrWt.get(0);
                                }

                                Wr.addAll(WrWt.get(0));
                                Wt.addAll(WrWt.get(1));
                            }
                        }
                    }
                }
            }
            return Wr;
        }
    //}

    private static int determineMaxBitWidth(LinkedList<Integer> targetConstants) {
        int maxBitWidth = 0;
        for (int constant : targetConstants) {
            int bitWidth = (int) (Math.log(Math.abs(constant)) / Math.log(2)) + 1;
            maxBitWidth = Math.max(maxBitWidth, bitWidth);
        }
        return maxBitWidth;
    }

    private static LinkedList<LinkedList<Integer>> synthesize(LinkedList<Integer> R, LinkedList<Integer> T) {
        boolean isAdded;
        do {
            isAdded = false;
            for (int k = 0; k < T.size(); k++) {
                int constant = T.get(k);
                if (canBeSynthesized(constant, R)) {
                    isAdded = true;
                    R.add(constant);
                    T.remove(k);
                    k--; // Adjust the index after removing the element
                }
            }
        } while (isAdded);

        LinkedList<LinkedList<Integer>> finalResult = new LinkedList<>();
        finalResult.add(R);
        finalResult.add(T);
        return finalResult;
    }

    private static boolean canBeSynthesized(int constant, LinkedList<Integer> R) {
        for (int element : R) {
            if (isDivisibleBy(constant, element)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isDivisibleBy(int number, int divisor) {
        return number % divisor == 0;
    }

    public static void main(String[] args) {
        LinkedList<Integer> T = new LinkedList<>();
        T.add(19);
        T.add(166);
        T.add(23);
        T.add(76);
        T.add(40);

        LinkedList<Integer> solution = BFSearch(T);

        if (solution != null) {
            System.out.println("Solution found:");
            for (int constant : solution) {
                System.out.println(constant);
            }
        } else {
            System.out.println("No solution found.");
        }
    }
}
