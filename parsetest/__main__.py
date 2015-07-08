from parse_rest.connection import register
from parse_rest.datatypes import Object
import time, sys, smtplib
register("OGgfMc5oniUrtTH8bmxfI7NhCxb4akmBseHKWI3m", "G0VqR1aB3zB3ykhz0skqS8RH2sybumo6sGcJqYF0")

#setup email that will text you
server = smtplib.SMTP( "smtp.gmail.com", 587 )
server.starttls()
server.login( '<gmail address>', '<gmail password>' )

counter = 0

class Printer(Object):
    pass

#get the object to use from parse
printer = Printer.Query.get(objectId="<your printer id>")

#set is printing to false letting the android device know the printing has stopped and the device has adjusted
printer.isPrinting = False
printer.save()

#continue checking parse for the android device to say its done analyzing
while not printer.isPrinting:
    #delay and get printer object
    printer = Printer.Query.get(objectId="<your printer id>")
    time.sleep(5)
    counter += 1
    print(counter)

error = printer.error
method = printer.method
errorPixels = printer.errorPixels
inside = printer.inside

if error > .01:
    server.sendmail('<name>', '<phone number @ carrier sms gateway>', 'Fail. Error was %s' % error)
    sys.exit(10) #if error is too high, then exit with code 10
else:
    server.sendmail('<name>', '<phone number @ carrier sms gateway>', 'Pass. Error was %s' % error)
    sys.exit(15)
