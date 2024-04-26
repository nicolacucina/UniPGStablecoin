async function createTokenChart(test=true) {
    // this chart seems to be one step behind the coin price chart
    const drawTokenLineChart = (data) => {
        
        const margin = { top: 40, right: 50, bottom: 25, left: 140 };
        const width = 500;
        const height = 400;
        const innerWidth = width - (margin.left + margin.right);
        const innerHeight = height - (margin.top + margin.bottom);
    
        const svg = d3
          .select("#token-line-chart")
          .append("svg")
          .attr("viewBox", `0 0 ${width} ${height}`);
    
        const innerChart = svg
          .append("g")
          .attr("transform", `translate(${margin.left}, ${margin.top})`)

        const xScale = d3
          .scaleLinear()
          .domain([0, d3.max(data, (d) => d.day)])
          .range([0, innerWidth]);
        
        const yScale = d3
          .scaleLinear()
          .domain([0, d3.max(data, (d) => d.token)])
          .range([innerHeight, 0]);
    
        const bottomAxis = d3
          .axisBottom(xScale)

        innerChart
          .append("g")
          .attr("class", "axis-x")
          .attr("transform", `translate(0, ${innerHeight})`)
          .call(bottomAxis);
    
        const leftAxis = d3.axisLeft(yScale);
        innerChart.append("g").attr("class", "axis-y").call(leftAxis);
    
        d3.selectAll(".axis-x text, .axis-y text")
          .style("font-family", "Roboto, sans-serif")
          .style("font-size", "14px");
    
        d3.selectAll(".axis-y text").attr("x", "-10px");

        svg
          .append("text")
          .text("Number of Tokens")
          .attr("y", 30)
          .attr("font-family", "sans-serif");
    
        svg
          .append("text")
          .text("Day")
          .attr("x", width - margin.right + 15)
          .attr("y", height - margin.bottom + 20)
          .attr("font-family", "sans-serif");
    
        /* Line Chart */
        const violet = "#8080ff";
        innerChart
          .selectAll("circle")
          .data(data)
          .join("circle")
          .attr("r", 4)
          .attr("cx", (d) => xScale(d.day))
          .attr("cy", (d) => yScale(d.token))
          .attr("fill", violet);
    
        const lineGenerator = d3
          .line()
          .x((d) => xScale(d.day))
          .y((d) => yScale(d.token));
    
        const curveGenerator = d3
          .line()
          .x((d) => xScale(d.day))
          .y((d) => yScale(d.token))
          .curve(d3.curveCatmullRom);
    
        innerChart
          .append("path")
          .attr("d", curveGenerator(data))
          .attr("fill", "none")
          .attr("stroke", violet);
      };

    if(test){
      d3.csv("./simulation/bin/data/run1.csv", (d) => {
        return {
          day: +d.Day,
          token: +d.Token,
        };
      }).then((data) => {
        drawTokenLineChart(data);
      });
    }else{
      // Get the token supply, the contract emits an event when the token supply changes
      // Logic is similar to the coin chart, look for transaction coming from the contract, inside
      // the transaction look for the event that changes the token supply, saved inside logs
      let latestBlock = await web3.eth.getBlockNumber();
      let dayrange = 20;
      let index = 0;
      let mydata = '[';
      while(dayrange>0){
        let temp = Number(latestBlock)-index;
        if (temp < 0){
          break;
        }else{
          let block = await web3.eth.getBlock(Number(latestBlock)-index);
          //console.log(block);
          let transaction = await web3.eth.getTransaction(block.transactions[0]); 
          if(transaction.to == UniPGStablecoinAddress && transaction.from == UniPGStablecoinMinter){
            console.log('Rebase transaction found in block ' + block.number);
            //console.log(transaction);
            let receipt = await web3.eth.getTransactionReceipt(transaction.hash);
            //console.log(receipt);
            let logs = receipt.logs;
            //console.log(logs);
            let sum = 0;
            for(let i =0; i<logs.length;i++){
              let log = logs[i];
              //console.log(log);
              //web3.eth.abi.decodeLog(inputs, hexString, topics)
              let decoded = await web3.eth.abi.decodeLog(UniPGStablecoinABI[3].inputs, log.data, log.topics);
              //console.log(decoded);
              let token = Number(BigInt(decoded['0']));
              //console.log(token);
              sum += token;
            }

            mydata += '{"day": ' + dayrange + ', "token": ' + sum + '},';
            dayrange--;
          }
          index++;
        }      
      }
      mydata = mydata.slice(0, -1) + ']';
      console.log(mydata);
      tokenSupplyData = JSON.parse(mydata);
      console.log(tokenSupplyData);
      
      drawTokenLineChart(tokenSupplyData);
    }
  }