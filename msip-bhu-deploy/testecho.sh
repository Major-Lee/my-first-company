#echo -n "please confirm your operation:(yes/no)"
#read name
#echo "hello $name, welcome to IT website"

#if read -t 5 -p "please input your name:"

#then
#  echo "hello $REPLY, welcome to come back here"
#else
#  echo "sorry , you are too slow "
#  exit
#fi
read -n1 -p "do you want to continue [Y/N]?"
case $REPLY in
	Y | y) echo
          echo "fine ,continue on ..";;
	N | n) echo 
          echo "OK, goodbye..."
          exit
          ;;
    *) echo
    	echo "only accept Y,y,N,n"
    	exit
    	;;      
          #exit
esac
echo "starting"