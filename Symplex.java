/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProyectoMO1;
import java.util.*;
/*
 *
 * @author Ricardo
 */
public class Symplex {
    double costos[][];
    double tecno[][];
    double recurso[][];
    double base[];
    int numRest;
    int numVarDes;
    
    public Symplex(double c[][], double t[][], double b[]){
        costos = c;
        tecno = t;      
        base = b;
    }
    
    
    
    public static String imprimirBase(String m[], boolean c[]){
        StringBuilder cad = new StringBuilder();
        cad.append("La base es: {");
        for(int i=0;i<m.length;i++)
            if(c[i]){
                cad.append(m[i]);
                if(i+1!=m.length)
                    cad.append(",");
            }
        cad.append("}");
        return cad.toString();
    }
    
    public static void main (String []args){
        Scanner lee = new Scanner(System.in);
        //declaro desde el inicio la tabla con la que haremos el método simplex
        Tabla tableau = new Tabla();
        System.out.print("¿Qué quieres hacer? Introduce: 'max' o 'min': ");
        String mm = lee.next();
        while(!mm.equals("min")&&!mm.equals("max")){
            System.out.print("No entendí, ¿me lo repites? Introduce: 'max' o 'min': ");
            mm = lee.next();
        }
        //la tabla reconoce que se quiere maximizar, ya está predeterminada a minimizar
        if(mm.equals("max"))
            tableau.setBoolean(true);
                
        //Comienza pidiendo información del problema al usuario.
        System.out.print("Introduce el número de variables : ");
        int v = Integer.parseInt(lee.next());
        System.out.print("Introduce el número de restricciones : ");
        int r = Integer.parseInt(lee.next());
        //la instancia tableau recibe el num de variables y restricciones
        tableau.setNumVar(v);
        tableau.setNumRest(r);
        //Declara la matriz de la función objetivo
        double funObjetivo[];
        funObjetivo= new double [v];
        String valori;
        //recibe los valores de la función ojetivo.
        for(int i=0;i<v;i++) /*&& Double.parseDouble(costoi) instanceof Double*/{
            System.out.print("\nIntroduce valor de X"+(i+1)+" : ");
            valori = lee.next();
            funObjetivo[i]=Double.parseDouble(valori);
        }
        //Se declara la matriz de tecnología
        double tecno[][] = new double[r][v];
        //Se declara la matriz de recursos
        double recursos[];
        recursos = new double[r];
        //Se declara la matriz de variables artificiales
        boolean artificiales[];
        artificiales = new boolean[r];
        //recibe los valores de la matriz de tecnología, de la indicadora de igualdades y de la matriz de recursos 
        for(int k=0;k<r;k++){
            for(int i=0;i<v;i++){
                System.out.print("\nIntroduce el valor X"+(i+1)+" de la restricción "+(k+1)+": ");
                tecno[k][i] = Double.parseDouble(lee.next()); 
            }
            System.out.print("\nIntroduce la desigualdad de la restricción "+(k+1)+": ");
            String temp = lee.next();
            // te pide repetir la acción en caso de error
            while(!temp.equals("=")&&!temp.equals(">=")&&!temp.equals("<=")){
                System.out.print("Perdón, no entendí.\nIntroduce nuevamente la desigualdad de la restricción ('>=','=','<=')"+(k+1)+": ");
                temp = lee.next();
            }
            if(temp.equals("=")||temp.equals(">="))
                artificiales[k] = true; 
            System.out.print("\nIntroduce el recurso de la restricción "+(k+1)+": ");
            recursos[k] = Integer.parseInt(lee.next());
        }
        
        //Se declara la matriz costos
        double c[];
        c = new double[v+r];
        //Se declara la matriz P
        double p[][];
        p = new double[r][v+r];
        //declaramos un arreglo que contendrá los nombres de las variables.
        String varString[];
        varString = new String[v+r];
        //Se imprime la matriz de tecnología con la que trabajaremos
        //las siguientes estructuras iterativas imprimiran la matriz y asignarán valores a la matriz P
        // también obtendremos la matriz de costos 
        System.out.printf("%1$8s","matrizP");
        for(int i=1;i<=v;i++){
            System.out.printf("%1$8s","X"+i);
            varString[i-1] = "X"+i;
        }
        for(int i=1;i<=r;i++){
            if(artificiales[i-1]){
                System.out.printf("%1$8s","R"+i);
                varString[v+i-1]="R"+i;
            }else{
                System.out.printf("%1$8s","S"+i);
                varString[v+i-1]="S"+i;
            }
        }
        System.out.println();
        for(int i = 1;i<=r;i++ ){
            if(artificiales[i-1])
                System.out.printf("%1$8s","R"+i);
            else
                System.out.printf("%1$8s","S"+i);
            for(int k = 1; k<=v;k++){
                p[i-1][k-1] = tecno[i-1][k-1];
                System.out.printf("%1$8.2f",tecno[i-1][k-1]);
                if(artificiales[i-1])
                    c[k-1]+=tecno[i-1][k-1];   
            }
            for(int k = 1; k<=r;k++ ){
                if(k==i+1){
                    if(artificiales[i-1])
                        c[k-1]+=1; //obtenemos la matriz de costos
                    p[i-1][v+k-1] = 1.0;
                    System.out.printf("%1$8.2f",1.0);
                }else{
                    p[i-1][v+k-1] = 0.0;
                    System.out.printf("%1$8.2f",0.0);
                }
            }
            System.out.println();
        }
        //Declaramos la variable que suma la matriz de recursos en la restricción
        double M = 0.0; 
        //obtenemos la variables M;
        for(int k = 0; k<r; k++){
            if(artificiales[k])
                M += recursos[k];        
        }
        //declaramos matriz booleana de la base e imprimos la base inicial
        boolean boolBase[];
        boolBase = new boolean[v+r];
        for(int i = 0; i<v+r; i++){
            if(i<v)
                boolBase[i] = false;
            else
                boolBase[i] = true;
        }
        System.out.print(imprimirBase(varString,boolBase));
        
        System.out.print("Primera Fase:\n ");
        //Obtenemos la matriz B
        double b[][];
        b = new double[r][r];
        
        int k = 0;
        for(int i = 0; i<v+r;i++)
            if(boolBase[i]){
                    for(int a=0;a<r;a++)
                        b[a][k] = p[a][i];
                    k++;
            }
        //Obtenemos la matriz Cb
        double cb[];
        cb = new double[r];
        int a = 0;
        for(int i = 0; i<v+r;i++)
            if(boolBase[i]){
                cb[a] = c[i];
                a++;
            }
        tableau.insertaMat(p, cb, b, c, recursos, boolBase, M);
        if(tableau.primeraFase())
            tableau.getSolucion();
        
    }
    
    
    
}

