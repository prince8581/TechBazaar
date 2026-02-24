function updateQuantity(btn, update) {
				var cartItem = btn.closest(".cart-row");
				var cartId = cartItem.getAttribute('data-id');
				var finalPrice = parseFloat(cartItem
						.querySelector('.final-price').value);
				var unitPrice = parseFloat(cartItem
						.querySelector(".price-per-unit").value);

				var qtyElement = cartItem.querySelector(".qty")

				let qty = parseInt(qtyElement.innerText);
				let max = 5;

				(update == 1) ? (qty == max ? alert("Maximum 5 Product Added Only") : qty += update)
						: qty == 1 ? alert("Atleast 1 Product Add.") : qty += update

				//update UI at realTime
				fetch('/UpdateQuantity/' + cartId + '?quantity=' + qty, {
					method : 'POST'
				})
				.then(res => res.json())
				.then(data =>{
					if(data.err){
						alert(data.err);
						qty = data.quantity;
					}
					else{
						qtyElement.innerText = qty;
						cartItem.querySelector('.updated-unit-price').innerText = parseFloat(qty
								* unitPrice).toFixed(1);
						cartItem.querySelector('.updated-final-price').innerText = parseFloat(qty
								* finalPrice).toFixed(1);
						
						document.getElementById("totalPrice").innerText ="₹"+ parseFloat(data.totalPrice).toFixed(1);
						document.getElementById("discount").innerText ="₹"+ parseFloat(data.totalPrice - data.finalPrice).toFixed(1);
						document.getElementById("finalPrice").innerText ="₹" +parseFloat( data.finalPrice.toFixed(1));
					}
				} )
			}

			

			function updateBuyNowQuantity(update)
							{
								var qtyElement=document.getElementById("buyNowQuantity");
								var pricePerUnit=parseFloat(document.getElementById("price-per-unit").value);
								var finalPrice=parseFloat(document.getElementById("final-price").value);
								
								let qty=parseInt(qtyElement.innerText);
								
								qty=qty+update;
								
								if(qty<1)
									qty=1;
								else if(qty>5){
									qty=5;
									alert("You Can only 5 quantity of this item")
								}
								
								qtyElement.innerText=qty;
								
								//Most Important for url QTY update
								const url = new URL(window.location.href);
								url.searchParams.set("qty",qty);
								window.history.replaceState({}, '', url); 
								
								document.getElementById("update-unit-price").innerText=parseFloat(qty*pricePerUnit).toFixed(1);
								document.getElementById("update-final-price").innerText=parseFloat(qty*finalPrice).toFixed(1);
								
								let shippingCharge = parseFloat(document.getElementById("shippingCharge").innerText);
								
								document.getElementById("totalPrice").innerText ="₹"+ parseFloat(qty*pricePerUnit).toFixed(1);
								document.getElementById("discount").innerText ="₹"+ parseFloat(qty*pricePerUnit- qty*finalPrice).toFixed(1);
								document.getElementById("finalPrice").innerText ="₹"+ parseFloat(qty*finalPrice+shippingCharge).toFixed(1);
							}
							
							//Place order function
							function placeOrder(){
								let form = document.getElementById("orderForm");
								let formData = new URLSearchParams(new FormData(form));
								
								fetch('/place-order', {
									method : 'POST',
									body : formData
								})
								.then(res => res.json())
								.then(data => {
									if(data.status == "FAILED"){
										alert(data.message);
									}
									if(data.status === "COD_SUCCESS"){
										alert("order Placed Successfully!");
										window.location.href="/Orders";
									}
									if(data.status === "ONLINE"){
										var options = {
												key : data.key, 
												amount : data.amount,
												currency : "INR",
												order_id : data.razorpayOrderId,
												handler : function(response){
													fetch('/verify-payment',{
														method : 'POST',
														headers : {"Content-Type" : "application/json"},
													    body : JSON.stringify(response)
													})
													.then(res => res.text())
													.then(data=>{
														alert(data);
														window.location.href= '/Orders';
													})
													.catch(err => alert(err.message));
												}
										};
										var rzp = new Razorpay(options);
										rzp.open();
									}
								});
							}