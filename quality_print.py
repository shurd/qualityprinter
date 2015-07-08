!import os
!notRunBefore = True
!while(self.p.printing and notRunBefore):
     !if (self.curlayer>0.350 and notRunBefore):
          !self.pause() #1179
          !self.onecmd('G0 X200 Y250 Z190 F2000')
          !time.sleep(15) #analysis here, may not work
          #insert path to __main__.py on your machine
          !exitcode = os.system("<path to __main__.py>")
          !if exitcode == 10: #end print
               !print "Error too high. Stopping print"
               !return
          !elif exitcode == 15:
               !print "Error within accepted bounds. Continuing print"
               !self.onecmd('G0 X200 Y250 Z10 F2000')
               !self.pause() #1179
          !notRunBefore = False
!print "Continuing Print"
