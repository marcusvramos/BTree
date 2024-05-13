import java.util.Random;

public class Aplicacao
{
    public static void main(String[] args) {
        BTree b = new BTree();

        for(int i=0;i<100;i++){
            b.inserir(i+1,i);
        }
        for(int i=0;i<=100;i+=2){
            b.excluir(i);
        }

        b.in_ordem();
    }
}