// JavaScript Document

$(function(){
	$(window.parent.document).find("#main_iframe").load(function(){
		var main = $(window.parent.document).find("#main_iframe");
		var thisheight =window.outerHeight-73-62-72;
		 				
		
//		var thisheight =  window.parent.document.body.scrollHeight-73-62-10;
	//	if(thisheight<900) thisheight=900;
		main.height(thisheight);	
		
		//获取登录状态
		$.post("/ViliageMgr/api/admin/mgr/checkLoginStatus", function(data, textStatus) {
				if (textStatus == 'success' && data != null) {
					if (data.code!=0) {
						window.parent.location.href='/ViliageMgr/man/login.html';
					}
				}
		}, 'json');
	});
})


function setFrameHeight(h){
	if(h)
	{
		var main = $(window.parent.document).find("#main_iframe");
		main.height(h+'px');
	}
	else
	{
		var main = $(window.parent.document).find("#main_iframe");
		var thisheight = $(document).height();
		main.height(thisheight);
	}
}

function resetFrameHeight(){
	var main = $(window.parent.document).find("#main_iframe");
	var thisHeight = $(document).height();
	var popHeight =$("#maxHeightDiv").height()+200;
//	if(popHeight>thisHeight) thisHeight=popHeight;
//	main.height(thisHeight);	
}

/*弹出窗口的处理*/
function popDivHandler(obj){
	var HandlerDiv=$(obj).next(".needPopDiv");
	$("#frame_shade").fadeIn(100);
	HandlerDiv.slideDown(200);	
	
	HandlerDiv.children(".PopContext").attr('id','maxHeightDiv');
	resetFrameHeight();	
}
function popDivClose(obj){
	var HandlerDiv=$(obj).parents(".needPopDiv");
	$("#frame_shade").fadeOut(200);
	HandlerDiv.slideUp(100);
	
	HandlerDiv.children(".PopContext").attr('id','');
	resetFrameHeight();
	event.stopPropagation()
}	

