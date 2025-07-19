import org.apache.commons.numbers.complex.Complex;

import java.util.Scanner;
import java.util.function.Function;
public class Newton {
    /* interceptThreshold is how close the value of a function at an x value has to be
    to zero to count as an x intercept. 0 means it has to be exactly 0 */
    //double interceptThreshold = 0.01;
    //h is the width of the interval for the secant line. Closer to 0 is better
    static Complex h = Complex.ofCartesian(0.01,0);
    static Complex lastSecantPoint = Complex.ofCartesian(0.5,0);
    //returns the secant line of the targetCurve centered around c with width h
    public static Function<Complex,Complex> getSecantLine(Function<Complex, Complex> targetCurve, Complex c) {
        lastSecantPoint = c;
        //returns f(c)+f'(c)(x-c)
        return x ->targetCurve.apply(c).add((((targetCurve.apply(c.add(h)).subtract(targetCurve.apply(c))).divide(h)).multiply((c.multiply(-1).add(x)))));
    }
    /*method for finding x intercept of a secant line. You need to know what the c and h values of
    the secant line were to find its x intercept. The h value should be final, so that is easy
    The c value changes line-to-line so that will be more difficult. Thinking of having the c for
    getSecantLine be retrieved from a static field, so that you can see the current value of the field
    to know the c value for the last secant line. Then you can find the x intercept easily.
     */
    public static Complex findSecantIntercept (Function<Complex, Complex> targetCurve) {
        //System.out.println("lastSecantPoint in findSecant Intercept is " + lastSecantPoint);
        Complex pointPrime = (targetCurve.apply(lastSecantPoint.add(h)).subtract(targetCurve.apply(lastSecantPoint))).divide(h);
        //System.out.println("lastSecantPoint is " + lastSecantPoint);
        return lastSecantPoint.subtract(targetCurve.apply(lastSecantPoint).divide(pointPrime));
    }
    //Rounds the real and imaginary parts of a complex number
    public static Complex roundCartesian (Complex complex, int digits) {
        double real = complex.getReal();
        double imaginary = complex.getImaginary();
        double factor = Math.pow(10,digits);
        real = Math.round(real*factor)/factor;
        imaginary = Math.round(imaginary*factor)/factor;
        return Complex.ofCartesian(real,imaginary);
    }
    //Function for semicircle: y=sqrt(1-x^2)
    public static Complex targetCurve(Complex x) {
        return x.pow(2).multiply(-1).add(1).sqrt();
    }
    //Will find the x intercept of a function using newton's method
    public static Complex getRoot(int iterations, Complex startingPoint, Function<Complex, Complex> targetCurve, int quadrant) {
        //NOTE: add functionality to find new intercept if intercept is found in the wrong quadrant
        lastSecantPoint = startingPoint;
        boolean rightQuadrant = true;
        //System.out.println("lastSecantPoint in getRoot before iteration is " + lastSecantPoint);
        for (int i = iterations; i>0; i--) {
            lastSecantPoint = findSecantIntercept(targetCurve);
            //System.out.println(lastSecantPoint);
        }
        return lastSecantPoint;
    }
    //Will find where a function and a line with a given angle intercept
    public static Complex getCosine(int iterations, Complex startingPoint, Function<Complex,Complex> targetCurve, double angle) {
        int quadrant;
        double principalAngle = angle % (2*Math.PI);
        if (principalAngle >= 0 && principalAngle < Math.PI/2d) {
            quadrant = 1;
        }
        else if (principalAngle >= Math.PI/2 && principalAngle < Math.PI) {
            quadrant = 2;
        }
        else if (principalAngle >= Math.PI && principalAngle < 3*Math.PI/2d) {
            quadrant = 3;
        }
        else {
            quadrant = 4;
        }
        return getRoot(iterations, startingPoint, x->(targetCurve.apply(x).multiply(-1)).add((x.multiply(Math.tan(angle)))), 1);
    }
    //Will find multiple cosine values within a certain range for a function
    public static void iterateCosine(int iterations, Complex startingPoint, Function<Complex,Complex> targetCurve, double startAngle, double endAngle, double totalAngles) {
        for (int i = 0; i <= totalAngles-1; i++) {
            double currentAngle = startAngle+(endAngle-startAngle)/(totalAngles-1)*i;
            Complex currentCos = getCosine(iterations,startingPoint,targetCurve, currentAngle);
            //if (currentCos != currentCos) {currentCos = 0;}
            // currentCos = Math.round(currentCos*1000)/(double)1000;
            //System.out.print(lastSecantPoint);
            System.out.print("(" + currentAngle + "," + roundCartesian(currentCos,5).getReal() + "), ");
        }
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        //System.out.println(findIntercept(scanner.nextDouble(),newton::targetCurve));
        //prints the secant approximation of targetCurve at 4 for x = next double

        /*System.out.println("What value of c do you want to center the approximation of f(x) around?");
        double startingPoint = scanner.nextDouble();
        System.out.println("How many iterations do you want to perform?");
        int iterations = scanner.nextInt();
        System.out.println("What angle do you want to find the cosine of in degrees?");
        double angle = scanner.nextDouble()*Math.PI/(double)180;*/
        iterateCosine(10,Complex.ofCartesian(1,0),Newton::targetCurve,0,Math.PI,7);
        //getCosine(10,.5,newton::targetCurve,120/(double)180*Math.PI);
        //System.out.println(getCosine(2,.5,newton::targetCurve,angle));
        //System.out.println("The Cosine of f(" + angle + ") approximated after " + iterations + " iterations starting from x = " + startingPoint + " is: " + getCosine(iterations, startingPoint, newton::targetCurve, angle) + ".");
        //value of secant line for next double: getSecantLine(newton::targetCurve, lastSecantPoint).apply(scanner.nextDouble())
        //System.out.println(Math.cos(angle));
    }
}
