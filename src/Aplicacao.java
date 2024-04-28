public class Aplicacao
{
    public static void main(String[] args) {
        BTree b = new BTree();

        for(int i=1; i<=50000000; i++)
            b.inserir(i, 0);
        System.out.println("Terminou!!!");

        b.in_ordem();
    }
}