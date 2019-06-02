Before run this application, please have your own server address and port number, add them to Client.java.
This is an assignment of Network Progtamming course of RMIT, using this project for plagiarism purposes is striclly prohibited and the author is not responsible for any plagiarism using this project.


1.	Start server on your server: java Server
Start Client on client side: java Client, then input first name. After user enter first name, the application will try to connect to server. If user want to start a second client, start a new terminal window and connect to netprog2, then run “java Client” to start a second client program.
 ![alt text](https://github.com/dhx2261/dhximages/blob/master/NP2.png)

2.	If connect to server successfully, the successful message will show, user will be waiting for other users to join the game, when there are three users in the queue or it reaches 60 seconds since first user to join the game, the game will start.
![alt text](https://github.com/dhx2261/dhximages/blob/master/NP3.png)
 
3.	When the game starts, server will display the correct answer on server side. 
![alt text](https://github.com/dhx2261/dhximages/blob/master/NP3.png)
The name of all clients in this round will be displayed on Client side.
Then user needs to input a number to guess. User can input single lowercase letter e to exit the game at any time during the game. User has 30 seconds to input a number to guess, if there is no input after 30 seconds, the game will disconnect with the server and terminates automatically.
![alt text](https://github.com/dhx2261/dhximages/blob/master/NP4.png)
 
4.	If the number user guessed is wrong, server will send a hint of correct answer to client. Once user guess the correct number or reach the maxim guess number, the user will be waiting for other users in this round to finish their game, once all users finish their game in this round, the screen will display the ranking result for this round (users who exit the game during the round will not be counted in the rank). Then the user can choose p to play again or q to exit the game. User has 30 seconds to input choice, if there is no input after 30 seconds, the game will disconnect with the server and terminates automatically.
![alt text](https://github.com/dhx2261/dhximages/blob/master/NP5.png)

Check log files:
Log files are stored in the same directory that server java file stored. To see game log, run ”cat gamelog.txt”, to see communication log, run “cat comlog.txt”
