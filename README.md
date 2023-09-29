# vending-machine-share build instructions
I checked Heroku and Netlify but I couldn't find a free service.

#IMPORTANT
THERE IS AN ADMIN INTERFACE IN THE APPLICATION, WHICH IS FOR ACCESSING THE VENDING MACHINE SETTINGS. THIS FEATURE IS HIDDEN FROM USERS. YOU CAN GO TO: http://localhost:3000/supplier TO ACCESS THE SUPPLIER PANEL.


Run spring application:

1-Go to vendingmachine

2- Run execute the command:
gradle bootRun

Run React application:

1- Go to vendingmachinefrontend folder, run those commands:

npm i

npm start

Also run MySQL server. MySQL used as database.

https://github.com/HalilMertKartal/vending-machine-share
