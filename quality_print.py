!notRunBefore = True
!while(self.p.printing and notRunBefore):
     !if (self.curlayer>0.350 and notRunBefore):
          !self.update_pos()
          !currentPositionX = self.current_pos[0]
          !currentPositionY = self.current_pos[1]
          !currentPositionZ = self.current_pos[2]
          !self.pause() #1179
          !self.onecmd('G0 X200 Y250 Z190 F2000')
          !time.sleep(15)
          run_script python #insert directory where the parse python script is located (parsetest directory)
          !self.pause()
          !notRunBefore = False
!print "Continuing Print"
