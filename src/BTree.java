public class BTree
{
    private No raiz;

    public BTree()
    {
        raiz = null;
    }

    private No navegarAteFolha(int info)
    {
        No atual = raiz;
        int pos;
        while (atual.getvLig(0)!=null)
        {
            pos = atual.procurarPosicao(info);
            atual = atual.getvLig(pos);
        }
        return atual;
    }

    private No localizarPai(No folha, int info)
    {
        No atual = raiz;
        No pai = atual;
        int pos;
        while(atual != folha)
        {
            pai = atual;
            pos = atual.procurarPosicao(info);
            atual = atual.getvLig(pos);
        }
        return pai;
    }

    private void split(No folha, No pai)
    {
        No cx1 = new No();
        No cx2 = new No();
        for(int i=0; i<No.m; i++)
        {
            cx1.setvInfo(i, folha.getvInfo(i));
            cx1.setvPos(i, folha.getvPos(i));
            cx1.setvLig(i, folha.getvLig(i));
        }
        cx1.setvLig(No.m, folha.getvLig(No.m));
        cx1.setTl(No.m);

        for(int i=No.m+1; i<2*No.m+1 ; i++)
        {
            cx2.setvInfo(i-(No.m+1), folha.getvInfo(i));
            cx2.setvPos(i-(No.m+1), folha.getvPos(i));
            cx2.setvLig(i-(No.m+1), folha.getvLig(i));
        }
        cx2.setvLig(No.m, folha.getvLig(2*No.m+1));
        cx2.setTl(No.m);

        if(pai==folha)
        {
            folha.setvInfo(0, folha.getvInfo(No.m));
            folha.setvPos(0, folha.getvPos(No.m));
            folha.setTl(1);
            folha.setvLig(0, cx1);
            folha.setvLig(1, cx2);
        }
        else
        {
            int pos = pai.procurarPosicao(folha.getvInfo(No.m));
            pai.remanejar(pos);
            pai.setvInfo(pos, folha.getvInfo(No.m));
            pai.setvPos(pos, folha.getvPos(No.m));
            pai.setTl(pai.getTl()+1);
            pai.setvLig(pos, cx1);
            pai.setvLig(pos+1, cx2);
            if(pai.getTl()>2*No.m)
            {
                folha=pai;
                pai=localizarPai(folha, folha.getvInfo(0));
                split(folha, pai);
            }
        }
    }

    public void inserir(int info, int posArq)
    {
        No folha, pai;
        int pos;
        if(raiz == null)
            raiz = new No(info,posArq);
        else
        {
            folha = navegarAteFolha(info);
            pos = folha.procurarPosicao(info);
            folha.remanejar(pos);
            folha.setvInfo(pos, info);
            folha.setvPos(pos, posArq);
            folha.setTl(folha.getTl() + 1);
            if(folha.getTl() > 2*No.m)
            {
                pai = localizarPai(folha, info);
                split(folha, pai);
            }
        }
    }

    private No localizarSubstitutoE(No no,int pos) {
        No folha = no;

        folha = folha.getvLig(pos);
        while(folha.getvLig(0)!=null)
            folha = folha.getvLig(folha.getTl());

        return folha;
    }


    private No localizarSubstitutoD(No no,int pos) {
        No folha = no;

        folha = folha.getvLig(pos+1);
        while(folha.getvLig(0)!=null)
            folha = folha.getvLig(0);

        return folha;
    }


    private void redistribuir(No no) {
        if(no!=null && no.getTl()<No.m && no!=raiz){
            No pai = localizarPai(no,no.getvInfo(0));
            // posicao onde se encontra o nó dentro da ligPai
            int posPai = pai.procurarPosicao(no.getvInfo(0));

            No irmaE = null,irmaD = null;
            if(posPai-1>=0)
                irmaE = pai.getvLig(posPai-1);
            if(posPai+1<=pai.getTl())
                irmaD = pai.getvLig(posPai+1);

            // posicao onde entrara o elemento troca dentro do no
            int pos;
            if(irmaE!=null && irmaE.getTl()>No.m){

                // colocando o elemento do pai dentro do no
                no.remanejar(0);
                no.setvPos(0,pai.getvPos(posPai-1));
                no.setvInfo(0,pai.getvInfo(posPai-1));
                // ocorre quando a irma que sera retirada tem uma ligação nela
                no.setvLig(0,irmaE.getvLig(irmaE.getTl()));
                no.setTl(no.getTl()+1);
                // colocando a irmaE dentro do lugar do elemento pai
                pai.setvInfo(posPai-1,irmaE.getvInfo(irmaE.getTl()-1));
                pai.setvPos(posPai-1,irmaE.getvPos(irmaE.getTl()-1));
                // diminuindo o tl da irmaE
                irmaE.setTl(irmaE.getTl()-1);
            }
            else
            if(irmaD!=null && irmaD.getTl()>No.m){

                no.setvPos(no.getTl(),pai.getvPos(posPai));
                no.setvInfo(no.getTl(),pai.getvInfo(posPai));
                // ocorre quando uma irma que sera retirada tem uma ligação nela
                no.setTl(no.getTl()+1);
                no.setvLig(no.getTl(),irmaD.getvLig(0));
                // colocando a irmaD dentro do lugar do elemento pai
                pai.setvInfo(posPai,irmaD.getvInfo(0));
                pai.setvPos(posPai,irmaD.getvPos(0));
                // remanejando irma para decrementar o tl
                irmaD.remanejarInverso(0);
                irmaD.setTl(irmaD.getTl()-1);
            }
            else
            if(irmaD!=null){
                // insercao do pai na irmaD
                irmaD.remanejar(0);
                irmaD.setvInfo(0,pai.getvInfo(posPai));
                irmaD.setvPos(0,pai.getvPos(posPai));
                irmaD.setTl(irmaD.getTl()+1);
                // insercao de nó em irmaD
                int i;
                for(i=no.getTl()-1;i>=0;i--){
                    irmaD.remanejar(0);
                    irmaD.setvInfo(0,no.getvInfo(i));
                    irmaD.setvPos(0,no.getvPos(i));
                    irmaD.setvLig(1,no.getvLig(i+1));
                }
                irmaD.setvLig(0,no.getvLig(0));
                irmaD.setTl(irmaD.getTl()+no.getTl());

                // diminui o tamanho do pai
                pai.remanejarInverso(posPai);
                pai.setTl(pai.getTl()-1);
                // se pai é zero quer dizer que era a raiz
                if(pai.getTl() == 0)
                    raiz = irmaD;
                else
                    redistribuir(pai);
            }
            else{
                // insercao do pai na irmaE
                pos = irmaE.procurarPosicao(pai.getvInfo(posPai-1));
                irmaE.remanejar(pos);
                irmaE.setvInfo(pos,pai.getvInfo(posPai-1));
                irmaE.setvPos(pos,pai.getvPos(posPai-1));
                irmaE.setTl(irmaE.getTl()+1);

                // insercao do nó em irmaE
                int i;
                for(i=0;i<no.getTl();i++){
                    irmaE.setvInfo(irmaE.getTl(),no.getvInfo(i));
                    irmaE.setvPos(irmaE.getTl(),no.getvPos(i));
                    irmaE.setvLig(irmaE.getTl(),no.getvLig(i));
                    irmaE.setTl(irmaE.getTl()+1);
                }
                irmaE.setvLig(irmaE.getTl(),no.getvLig(i));


                // diminui o tamanho do pai
                pai.remanejarInverso(posPai-1);
                pai.setvLig(posPai-1,irmaE);
                pai.setTl(pai.getTl()-1);

                if(pai.getTl()==0)
                    raiz = irmaE;
                else
                    redistribuir(pai);
            }
        }
    }


    private void excluirNo(int info, No no) {
        int pos = no.procurarPosicao(info);
        // nao é folha
        if(no.getvLig(0)!=null){
            No subE = localizarSubstitutoE(no,pos);
            No subD = localizarSubstitutoD(no,pos);
            if(subE.getTl()>No.m){
                no.setvInfo(pos,subE.getvInfo(subE.getTl()-1));
                no.setvPos(pos,subE.getvPos(subE.getTl()-1));
                subE.setTl(subE.getTl()-1);
            }else{
                no.setvInfo(pos,subD.getvInfo(0));
                no.setvPos(pos,subD.getvPos(0));
                subD.remanejarInverso(0);
                subD.setTl(subD.getTl()-1);
                redistribuir(subD);
            }
        }
        // é folha
        else{
            no.remanejarInverso(pos);
            no.setTl(no.getTl()-1);
            if(no.getTl() == 0)
                raiz = null;
            else
                redistribuir(no);
        }
    }

    private No localizarNo(int info){
        No no = raiz;
        int pos;
        boolean achou = false;
        while(no!=null && !achou){
            pos = no.procurarPosicao(info);
            if(pos<no.getTl() && no.getvInfo(pos) == info)
                achou = true;
            else
                no = no.getvLig(pos);
        }

        return no;
    }
    public void excluir(int info){
        No folha = localizarNo(info);
        if(folha!=null) {
            excluirNo(info, folha);
        }
    }

    public void in_ordem()
    {
        in_ordem(raiz);
    }

    private void in_ordem(No no)
    {
        if(no!=null)
        {
            for(int i=0; i<no.getTl(); i++)
            {
                in_ordem(no.getvLig(i));
                System.out.println(no.getvInfo(i));
            }
            in_ordem(no.getvLig(no.getTl()));
        }
    }
}