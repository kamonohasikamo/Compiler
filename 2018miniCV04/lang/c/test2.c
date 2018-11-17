const int SIZE = 20;
int a, f[20], *i;
 
a = SIZE;
while(a >= SIZE){
    input &a;
    if(a == 0){
        a = SIZE;
    }else if(a < 2){
        a = SIZE + 1;
    }else{
 
    }
}
 
f[0] = 1;
f[1] = 1;
 
*i = 2;
while(*i < a){
    f[*i] = f[*i - 1] + f[*i - 2];
    *i = *i + 1;
}
 
output f[*i - 1];