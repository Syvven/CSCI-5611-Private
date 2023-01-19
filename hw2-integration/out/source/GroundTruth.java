/* autogenerated by Processing revision 1286 on 2022-10-15 */
import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class GroundTruth extends PApplet {

//Different functions to test with PDEs
//You need to specify both the derivative you want to numerically integrate (dxdt)
// and its antiderivative (which is used to evaluate accuracy).

//TODO:
//  -Try: dx/dx = 2*t*cos(t*t)
//        dx/dt = 2
//        dx/dt = 2*t
//        dx/dt = t*t*t
//        dx/dt = x
//        dx/dt = sin(t) + t*cos(t)
 public float dxdt(float t, float x){ //The function actual_x_of_t() should be an antiderivative of this function
  return x;
}

//In practice the derivative will typically be complex enough that we don't know the actual answer
//   but for this assignment, let's practice with simple functions we know the antiderivative of.
//   We use this known antiderivative to compute the error of the numerical approximations.
//Note: There is a family of antiderivative functions up-to a shift (the test-harness code auto-detects the shift)
 public float actual_x_of_t(float t){
  return exp(t); //The derivative of this function should be placed in dxdt!
}

//Returns a list of the actual values from t_start to t_end (also ignores shifts as the "actual" function)
 public ArrayList<Float> actualList(float t_start, int n_steps, float dt){
  ArrayList<Float> xVals = new ArrayList<Float>();
  float t = t_start;
  xVals.add(actual_x_of_t(t));
  for (int i = 0; i < n_steps; i++){
    t += dt;
    xVals.add(actual_x_of_t(t));
  }
  return xVals;
}
//CSCI 5611 HW 2 PDE Library
//Look at GroundTruth.pde and Integrator.pde for more instructions.

 public void RunComparisons() {
  println("==========\nComparison Against the Ground Truth\n==========");
  println();
  
  //Integrate from t_start to t_end
  float t_start = 0;
  float x_start = actual_x_of_t(t_start);
  float dt = 0.1f;
  int n_steps = 10;
  float t_end = t_start + n_steps * dt;
  
  float x_end;
  ArrayList<Float> x_all;
  ArrayList<Float> x_actual = actualList(t_start,n_steps,dt);
  float actual_end = actual_x_of_t(t_end);

  
  //Integrate using Eulerian integration
  println("Eulerian: ");
  x_end = eulerian(t_start,x_start,n_steps,dt);
  println("f(t) for t =",t_end,"is",x_end," Ground truth:", actual_end," Error is", actual_end-x_end);

  println("Printing Each Step--");
  x_all = eulerianList(t_start,x_start,n_steps,dt);
  println("Approx:",x_all);
  println("Actual:",x_actual);
  

  //Integrate using Midpoint integration
  println("\nMidpoint: ");
  x_end= midpoint(t_start,x_start,n_steps,dt);
  println("f(t) for t =",t_end,"is",x_end," Ground truth:", actual_end," Error is", actual_end-x_end);
  
  println("Printing Each Step--");
  x_all = midpointList(t_start,x_start,n_steps,dt);
  println("Approx:",x_all);
  println("Actual:",x_actual);
  
  
  //Integrate using RK4 (4th order Runge–Kutta)
  println("\nRK4: ");
  x_end= rk4(t_start,x_start,n_steps,dt);
  println("f(t) for t =",t_end,"is",x_end," Ground truth:", actual_end," Error is", actual_end-x_end);
  
  println("Printing Each Step--");
  x_all = rk4List(t_start,x_start,n_steps,dt);
  println("Approx:",x_all);
  println("Actual:",x_actual);
  
 
  //For comparison, this is Heun's method, a different 2nd order method (similar to midpoint)
  println("\nHeun: ");
  x_end= heun(t_start,x_start,n_steps,dt);
  println("f(t) for t =",t_end,"is",x_end," Ground truth:", actual_end," Error is", actual_end-x_end);

  println("Printing Each Step--");
  x_all = heunList(t_start,x_start,n_steps,dt);
  println("Approx:",x_all);
  println("Actual:",x_actual);
}


 public void setup(){
  // Test code for homework 2.
  
   // Compare the numerical integration results with actual antiderivatives 
  RunComparisons();
}
//Integrate Various ODEs
//CSCI 5611 ODE/PDE Integration Sample Code
// Stephen J. Guy <sjguy@umn.edu>

//TODO:
// To help you get started, we've implemented Eulerian integration, RK4, and Heun's method for you.
// We've also implemented a special version of RK4 that returns a list of all of the
//   intermediate values of x between t_start and t_end (for every dt sample).
// You need to implement:
//    -The midpoint method of integration
//    -A version of midpoint integration which returns a list of intermediate values
//    -A version of Eulerian integration which returns a list of intermediate values

//Eulerian Integration
//Assumes that the current slope, dx/dt, holds true for the entire range dt
 public float eulerian(float t_start, float x_start, int n_steps, float dt){
  float x = x_start;
  float t = t_start;
  for (int i = 0; i < n_steps; i++){
    x += dxdt(t,x)*dt;
    t += dt;
  }
  return x;
}

//Midpoint method
//Simulate forward 1/2 of a timestep with eulerian integration
//Compute the derivative of the simulation 1/2 of a timestep ahead
//Use the derivative from a 1/2 timestep ahead back at the original state
 public float midpoint(float t_start, float x_start, int n_steps, float dt){
  // Compute the value of x at time t_end using midpoint integration
  float x = x_start;
  float t = t_start;
  for (int i = 0; i < n_steps; i++) {
    float m1 = dxdt(t, x);
    float m2 = dxdt(t + dt/2, x + dt*m1*0.5f);
    x += m2*dt;
    t += dt;
  }
  return x;
}

//RK4 - or "The Runge–Kutta method"
//https://en.wikipedia.org/wiki/Runge%E2%80%93Kutta_methods#The_Runge%E2%80%93Kutta_method
// It's essentially a mid-point of midpoints methods, and provides 4th order accuracy
// RK4 is very popular in practice as it provides a nice balance between stability and computational speed
 public float rk4(float t_start, float x_start, int n_steps, float dt){
  float x = x_start;
  float t = t_start;
  for (int i = 0; i < n_steps; i++){
    float k1 = dxdt(t,x);
    float k2 = dxdt(t+dt/2,x+dt*k1/2);
    float k3 = dxdt(t+dt/2,x+dt*k2/2);
    float k4 = dxdt(t+dt,x+dt*k3);
    x += (k1+2*k2+2*k3+k4)*dt/6;
    t += dt;
  }
  return x;
}

//Heun's method (https://en.wikipedia.org/wiki/Heun%27s_method)
//Use the current slope to predict the next x
//Find the slope at the next x
//Re-run the current x with the average of the current slope and the next slope
 public float heun(float t_start, float x_start, int n_steps, float dt){ //Heun's method
  float x = x_start;
  float t = t_start;
  for (int i = 0; i < n_steps; i++){
    float curSlope = dxdt(t,x);
    float x_next = x + curSlope*dt; //Take a normal Euler step, but then...
    float nextSlope = dxdt(t+dt,x_next); //Look at the slope at where we land.
    x += dt*(curSlope+nextSlope)/2; //Average the current slope and the expected next slope
    t += dt;
  }
  return x;
}

 public ArrayList<Float> heunList(float t_start, float x_start, int n_steps, float dt){ //Heun's method
  ArrayList<Float> xVals = new ArrayList<Float>();
  float x = x_start;
  float t = t_start;
  xVals.add(x);
  for (int i = 0; i < n_steps; i++){
    float curSlope = dxdt(t,x);
    float x_next = x + curSlope*dt; //Take a normal Euler step, but then...
    float nextSlope = dxdt(t+dt,x_next); //Look at the slope at where we land.
    x += dt*(curSlope+nextSlope)/2; //Average the current slope and the expected next slope
    t += dt;
    xVals.add(x);
  }
  return xVals;
}

//Returns a list of the computed values from t_start to t_end using Eulerian integration
 public ArrayList<Float> eulerianList(float t_start, float x_start, int n_steps, float dt){
  ArrayList<Float> xVals = new ArrayList<Float>();
  // Place each step of Eulerian integration in a list
  float x = x_start;
  float t = t_start;
  xVals.add(x);
  for (int i = 0; i < n_steps; i++){
    x += dxdt(t,x)*dt;
    t += dt;
    xVals.add(x);
  }
  return xVals;
}

//Returns a list of the computed values from t_start to t_end using Midpoint integration
 public ArrayList<Float> midpointList(float t_start, float x_start, int n_steps, float dt){
  ArrayList<Float> xVals = new ArrayList<Float>();
  float x = x_start;
  float t = t_start;
  xVals.add(x);
  for (int i = 0; i < n_steps; i++) {
    float m1 = dxdt(t, x);
    float m2 = dxdt(t + dt*0.5f, x + (dt*m1*0.5f));
    x += m2*dt;
    t += dt;
    xVals.add(x);
  }
  return xVals;
}

//Returns a list of the computed values from t_start to t_end using RK4 integration
 public ArrayList<Float> rk4List(float t_start, float x_start, int n_steps, float dt){
  ArrayList<Float> xVals = new ArrayList<Float>();
  float x = x_start;
  float t = t_start;
  xVals.add(x);
  for (int i = 0; i < n_steps; i++){
    float k1 = dxdt(t,x);
    float k2 = dxdt(t+dt/2,x+dt*k1/2);
    float k3 = dxdt(t+dt/2,x+dt*k2/2);
    float k4 = dxdt(t+dt,x+dt*k3);
    x += (k1+2*k2+2*k3+k4)*dt/6;
    t += dt;
    xVals.add(x);
  }
  return xVals;
}


  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "GroundTruth" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
