<rui:popupWindow componentId="${uiElement.component.name}" width="${uiElement.width}" height="${uiElement.height}" resizable="${uiElement.resizable}" 
${uiElement.minWidth != 0?"minWidth='"+uiElement.minWidth+"'":""} ${uiElement.maxWidth != 0?"maxWidth='"+uiElement.maxWidth+"'":""}
${uiElement.minHeight != 0?"minHeight='"+uiElement.minHeight+"'":""} ${uiElement.maxHeight != 0?"maxHeight='"+uiElement.maxHeight+"'":""}
${uiElement.x != 0?"x='"+uiElement.x+"'":""} ${uiElement.y != 0?"y='"+uiElement.y+"'":""}
></rui:popupWindow>