<%-- 
    Document   : header-bottom
    Created on : Feb 24, 2013, 9:08:18 AM
    Author     : Alex O'Ree
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<hr>

<footer>
    <p>&copy; 2013 The Apache Software Foundation. All Rights Reserved.</p>
</footer>
 <div class="modal hide fade" id="confirmDialog">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h3>Digital Signature Warning</h3>
        </div>
        <div class="modal-body">
            <p>This item is digitally signed. This means that when saving your changes, all existing signatures will become invalid and 
            will automatically be excluded from the save process. </p>
        </div>
        <div class="modal-footer">
            <a href="#" class="btn">Close</a>
            <a href="javascript:deleteBusiness();$('#confirmDialog').modal('hide');" class="btn btn-primary">Save changes</a>
        </div>
    </div>
</div> <!-- /container -->
</form>
</body>
</html>

