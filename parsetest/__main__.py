from parse_rest.connection import register
from parse_rest.datatypes import Object
register("OGgfMc5oniUrtTH8bmxfI7NhCxb4akmBseHKWI3m", "G0VqR1aB3zB3ykhz0skqS8RH2sybumo6sGcJqYF0")#,"drXLqyGd9QpmNzvqPI8QSWpyoRrfwPnX8yk72XXL")
counter = 0

class Printer(Object):
    pass

#get the object to use from parse
printer = Printer.Query.get(objectId="Umx4FElpfg")

#set is printing to false letting the android device know the printing has stopped and the device has adjusted
printer.isPrinting = False
printer.save()

#continue checking parse for the android device to say its done analyzing
while not printer.isPrinting:
    #delay and get printer object
    printer = Printer.Query.get(objectId="Umx4FElpfg")
    counter += 1
    if counter % 50 == 0:
        print(counter/50)

error = printer.error
print(error)
#after loop resume
print("done")
