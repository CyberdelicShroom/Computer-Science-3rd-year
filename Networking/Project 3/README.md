How to compile and execute the program:

* To compile, type 'make' in terminal and press enter.

To run with preset configurations (preset args) from .sh files:

* To run the NATBox with 600000ms table refresh time, type './NATBox.sh' in the terminal and press enter.
* To run a new client on localhost with port 6969, type './clientInt.sh' for an Internal client or './clientExt.sh for an External client.

To run with manual configurations (custom args):

* To run the NATBox, type the following:

  * java NAT.NATBox <**Table_refresh_time**>

* To run a new client, type the following:

  * java NAT.Client <**host**> 6969 <**int/ext**>

(6969 is the port and int/ext is for an internal client or external client)

To send messages between clients, type the following into console:
* send <**IP_of_destination**>
