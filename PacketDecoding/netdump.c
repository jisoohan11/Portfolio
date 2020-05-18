/* 
 * Jisoo Han
 * 877800952
 * Spring 2020
 * Submission date = 4/23/2020
 */
#define RETSIGTYPE void
#include <sys/types.h>
#include <sys/time.h>
#include <netinet/in.h>
#include <pcap.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <ctype.h>

#ifndef setsignal_h
#define setsignal_h

RETSIGTYPE (*setsignal(int, RETSIGTYPE (*)(int)))(int);
#endif

char cpre580f98[] = "netdump";

void raw_print(u_char *user, const struct pcap_pkthdr *h, const u_char *p);

int packettype;

char *program_name;

/* Externs */
extern void bpf_dump(const struct bpf_program *, int);

extern char *copy_argv(char **);

/* Forwards */
 void program_ending(int);

/* Length of saved portion of packet. */
int snaplen = 1500;;

static pcap_t *pd;

extern int optind;
extern int opterr;
extern char *optarg;
int pflag = 0, aflag = 0;

int numip=0;
int numarp=0;
int numicmp=0;
int numtcp=0;
int numSMTP=0;
int numPOP=0;
int numHTTP=0;
int numIMAP=0;
int numDNS=0;
main(int argc, char **argv)
{
	int cnt, op, i, done = 0;
	bpf_u_int32 localnet, netmask;
	char *cp, *cmdbuf, *device;
	struct bpf_program fcode;
	 void (*oldhandler)(int);
	u_char *pcap_userdata;
	char ebuf[PCAP_ERRBUF_SIZE];

	cnt = -1;
	device = NULL;
	
	if ((cp = strrchr(argv[0], '/')) != NULL)
		program_name = cp + 1;
	else
		program_name = argv[0];

	opterr = 0;
	while ((i = getopt(argc, argv, "pa")) != -1)
	{
		switch (i)
		{
		case 'p':
			pflag = 1;
		break;
		case 'a':
			aflag = 1;
		break;
		case '?':
		default:
			done = 1;
		break;
		}
		if (done) break;
	}
	if (argc > (optind)) cmdbuf = copy_argv(&argv[optind]);
		else cmdbuf = "";

	if (device == NULL) {
		device = pcap_lookupdev(ebuf);
		if (device == NULL)
			error("%s", ebuf);
	}
	pd = pcap_open_live(device, snaplen,  1, 1000, ebuf);
	if (pd == NULL)
		error("%s", ebuf);
	i = pcap_snapshot(pd);
	if (snaplen < i) {
		warning("snaplen raised from %d to %d", snaplen, i);
		snaplen = i;
	}
	if (pcap_lookupnet(device, &localnet, &netmask, ebuf) < 0) {
		localnet = 0;
		netmask = 0;
		warning("%s", ebuf);
	}
	/*
	 * Let user own process after socket has been opened.
	 */
	setuid(getuid());

	if (pcap_compile(pd, &fcode, cmdbuf, 1, netmask) < 0)
		error("%s", pcap_geterr(pd));
	
	(void)setsignal(SIGTERM, program_ending);
	(void)setsignal(SIGINT, program_ending);
	/* Cooperate with nohup(1) */
	if ((oldhandler = setsignal(SIGHUP, program_ending)) != SIG_DFL)
		(void)setsignal(SIGHUP, oldhandler);

	if (pcap_setfilter(pd, &fcode) < 0)
		error("%s", pcap_geterr(pd));
	pcap_userdata = 0;
	(void)fprintf(stderr, "%s: listening on %s\n", program_name, device);
	if (pcap_loop(pd, cnt, raw_print, pcap_userdata) < 0) {
		(void)fprintf(stderr, "%s: pcap_loop: %s\n",
		    program_name, pcap_geterr(pd));
		exit(1);
	}
	pcap_close(pd);
	exit(0);
}

/* routine is executed on exit */
void program_ending(int signo)
{
	struct pcap_stat stat;
	if (pd != NULL && pcap_file(pd) == NULL) {
		(void)fflush(stdout);
		putc('\n', stderr);
		if (pcap_stats(pd, &stat) < 0)
			(void)fprintf(stderr, "pcap_stats: %s\n",
			    pcap_geterr(pd));
		                // if(type == 0x0800) ip++;
	        else {
			(void)fprintf(stderr, "%d packets received by filter\n",
			    stat.ps_recv);
			(void)fprintf(stderr, "%d packets dropped by kernel\n",
			    stat.ps_drop);
			//(void)fprintf(stderr, "%d total ip\n",ip);
		}
	}
	printf("Number of Total IP is %d\n", numip);
	printf("Number of Total ARP is %d\n", numarp);
	printf("Number of Total ICMP is %d\n", numicmp);
	printf("Number of Total TCP is %d\n", numtcp);
	printf("Number of Total SMTP is %d\n", numSMTP);
	printf("Number of Total POP is %d\n", numPOP);
	printf("Number of Total HTTP is %d\n", numHTTP);
	printf("Number of Total IMAP is %d\n", numIMAP);
	printf("Number of Total DNS Packet is %d\n", numDNS);
	exit(0);
}

/* Like default_print() but data need not be aligned */
void
default_print_unaligned(register const u_char *cp, register u_int length)
{
	register u_int i, s;
	register int nshorts;

	nshorts = (u_int) length / sizeof(u_short);
	i = 0;
	while (--nshorts >= 0) {
		if ((i++ % 8) == 0)
			(void)printf("\n\t\t\t");
		s = *cp++;
		(void)printf(" %02x%02x", s, *cp++);
	}
	if (length & 1) {
		if ((i % 8) == 0)
			(void)printf("\n\t\t\t");
		(void)printf(" %02x", *cp);
	}
}

/*
 * By default, print the packet out in hex.
 */
void
default_print(register const u_char *bp, register u_int length)
{
	register const u_short *sp;
	register u_int i;
	register int nshorts;

	if ((long)bp & 1) {
		default_print_unaligned(bp, length);
		return;
	}
	sp = (u_short *)bp;
	nshorts = (u_int) length / sizeof(u_short);
	i = 0;
	while (--nshorts >= 0) {
		if ((i++ % 8) == 0)
			(void)printf("\n\t");
		(void)printf(" %04x", ntohs(*sp++));
	}
	if (length & 1) {
		if ((i % 8) == 0)
			(void)printf("\n\t");
		(void)printf(" %02x", *(u_char *)sp);
	}
}

void raw_print(u_char *user, const struct pcap_pkthdr *h, const u_char *p)
{
        u_int length = h->len;
        u_int caplen = h->caplen;
        uint16_t e_type;
	int a,b,c,i,q,k;
	int temp=p[23];
	int bi[5];
        printf("---Begin Ethernet Header Decode---\n");
        printf("DEST Address = %02X: %02X: %02X: %02X: %02X: %02X\n", p[0], p[1], p[2], p[3], p[4], p[5]);
	printf("SOURCE Address = %02X: %02X: %02X: %02X: %02X: %02X\n", p[6], p[7], p[8], p[9], p[10], p[11]);
   
	e_type = p[12] * 256 + p[13];
	printf("E_Type = 0x%04X ", e_type);
	
	if (e_type == 0x0800) printf("\npayload -> IPv4\n");
	else printf("\npayload -> ARP\n");
	printf("---End Ethernet Header Decode---\n");
	
	if(e_type == 0x0800){
	numip++;
	printf("\n\t---Begin IP Decode---\n");
	printf("\tVersion number = %X \n", p[14]/17);
	printf("\tHeader Length = 20bytes\n");
	printf("\tType of Service = 0x%02X \n", p[15]);
	uint16_t tlength;
	tlength= p[16] * 256 + p[17];
       	printf("\tTotal Length = %d bytes\n", tlength);
        printf("\tID = 0x%02X%02X \n", p[18], p[19]);
	printf("\tFlag = ");
	for(q =0; temp>0; q++){
		bi[q] = temp%2;
		temp = temp /2;}
	for(k= q-1; k>=0; k--){
		printf("%d", bi[k]);
	}
	printf("\n\t\tD Flag - Don't Fragment\n");
	printf("\toffset = 0 bytes\n");
	printf("\tTTL = %d\n", p[22]);
	printf("\tprotocol = %01d ", p[23]);
	if(p[23]==06) printf("->TCP\n");
	else printf("-> ICMP\n");
	printf("\tChecksum = 0x%02X%02X \n", p[24],p[25]);
	printf("\tSource IP Address = %d.%d.%d.%d\n",p[26],p[27],p[28],p[29]);
	printf("\tDestination  IP Address = %d.%d.%d.%d\n",p[30],p[31],p[32],p[33]);
	printf("\n\t---End IP Decode---\n");

	if(p[23]==06){
	numtcp++;
	printf("\n\t\t---Begin TCP Decode---\n");
	printf("\t\tSource Port Number = %02d\n", p[34]*256+p[35]);
	printf("\t\tDestination Port Number = %02d\n", p[36]*256+p[37]);
	printf("\t\tSequence Number = 0X%02X%02X%02X%02X\n", p[38],p[39],p[40],p[41]);
	printf("\t\tAcknowledgement Number = 0X%02X%02X%02X%02X\n" ,p[42],p[43],p[44],p[45]);
	printf("\t\tHeader Legnth = 40 bytes\n");
	int tempp = p[47];
	printf("\t\tFlags = ");
	for( q =0; tempp>0; q++){
		bi[q] = tempp%2;
		tempp = tempp /2;}
	for(k=q-1; k>=0; k--){
		printf("%d", bi[k]);
	}
	printf("\n");
	if(bi[0]==1) printf("\t\tURG\n");
	if(bi[1]==1)printf("\t\tACK\n");
	if(bi[2]==1)printf("\t\tPSH\n");
	if(bi[3]==1)printf("\t\tRST\n");
	if(bi[4]==1)printf("\t\tSYN\n");
	if(bi[5]==1)printf("\t\tFIN\n");
	printf("\t\tWindow size = %02d\n",p[48]*256+p[49]);
	printf("\t\tChecksum = 0x%02X%02X\n",p[50],p[51]);
	printf("\t\tUrgent Pointer = %04d\n", p[52] *256+p[53]);
	printf("\t\tOptions = 0X%02X%02X %02X%02X %02X%02X %02X%02X %02X%02X %02X%02X ", p[54],p[55],p[56],p[57],p[58],p[59],p[60],p[61],p[62],p[63],p[64],p[65]);
	printf("\n\t\tPayloads = ");
	for(i=54; i<length; i++){
		printf("%02X",p[i]);
		if(i%2 !=0) printf(" ");
	}
	if(((p[34]*256+p[35])==25) || (((p[36]*256+p[37])==25))){
		numSMTP++;
		printf("\n\t\tSMTP payload in ASCII = ");
		for(i=54; i<length; i++){
			if(isprint(p[i])!=0)
			printf("%c", p[i]);
	}
	}
	if(((p[34]*256+p[35])==80) || (((p[36]*256+p[37])==80))){
		numHTTP++;
		printf("\n\t\tHTTP payload in ASCII = ");
		for(i=54; i<length; i++){
			if(isprint(p[i])!=0)
				printf("%c", p[i]);
		}
	}
	if(((p[34]*256+p[35])==110) || (((p[36]*256+p[37])==110))){
		numPOP++;
		printf("\n\t\tPOP payload in ASCII = ");
		for(i=54; i<length; i++){
			if(isprint(p[i])!=0)
				printf("%c",p[i]);
		}
	}
	if(((p[34]*256+p[35])==143) || (((p[36]*256+p[37])==143))){
		numIMAP++;
		printf("\n\t\tIMAP payload in ASCII = ");
		for(i=54;i<length;i++){
			if(isprint(p[i])!=0)
				printf("%c",p[i]);
		}
	}
	if(((p[34]*256+p[35])==53) || (((p[36]*256+p[37])==53))){
		numDNS++;
	}
	printf("\n\t\t---End TCP Decode---\n");
	}

	if(p[23] != 06){
	numicmp++;
	printf("\t\t---Begin ICMP Decode---\n");
	printf("\t\tType = %02X\n",p[34]);
	printf("\t\tCode = %02X\n",p[35]);
	printf("\t\tChecksum = 0x%02X%02X\n",p[36],p[37]);
	if((p[34]==0 && p[35] ==0)||((p[34]==8) && p[35] ==0)||((p[34]==13)&&p[35]==0)||((p[34]==14)&&p[35]==0)){
		printf("\t\tParameter = %02X%02X+%02X%02X\n", p[38], p[39], p[40],p[41]);}
	else{
		printf("\t\tParameter = %02X%02X02X%02X\n", p[38],p[39],p[40],p[41]);
	}			
	printf("\t\tInformation = ");
	for(i=42; i<length; i++){
		printf("%02X", p[i]);
		if(i%2 !=0) printf(" ");
	}
	printf("\n\t\t---End ICMP Decode---\n");
		}
	}


	else{
	numarp++;
	printf("\t\t---Begin ARP Decode---\n");
	printf("\t\tHardware Type = %02X%02X\n", p[14],p[15]);
	printf("\t\tProtocol Type = %02X%02X\n", p[16],p[17]);
	printf("\t\tHardware lenghth = %02X\n", p[18]);
	printf("\t\tProtocol length= %02X\n", p[19]);
	printf("\t\tOperation = %02X%02X\n", p[20],p[21]);
	printf("\t\tSender Hardware Address = %02X:%02X:%02X:%02X:%02X:%02X\n",p[22],p[23],p[24],p[25],p[26],p[27]);
	printf("\t\tSender Protocol Address = %d.%d.%d.%d\n",p[28],p[29],p[30],p[31]);
	printf("\t\tTarget Hardware Address = %02X:%02X:%02X:%02X:%02X:%02X\n",p[32],p[33],p[34],p[35],p[36],p[37]);
	printf("\t\tTarget Protocol Address = %d.%d.%d.%d\n",p[38],p[39],p[40],p[41]);
	printf("\t\tData = ");
	for(i=42; i<length; i++){
		printf("%02X", p[i]);
		if(i%2 !=0) printf(" ");
	}
	printf("\n\t\t---End ARP Decode---");
	}


	default_print(p, caplen);
        putchar('\n');
	
}	
