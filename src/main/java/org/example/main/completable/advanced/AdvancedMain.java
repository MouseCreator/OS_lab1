package org.example.main.completable.advanced;

public class AdvancedMain {

    /*
    TODO:
    1. Add locks controller --DONE
    2. Implement status handling --DONE
    3. Fix critical error extra info --DONE
    4. Fix memoization map --DONE
    5. Fix exits --DONE
    6. Fix console output -- use \r?
     */
    public static void main(String[] args) {
        AdvancedController controller = new AdvancedController();
        controller.start();
    }

}
