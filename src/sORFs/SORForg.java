package sORFs;

import javax.management.Query;

import format.Flat;

public class SORForg {

	public Flat getResult() {
		Flat results = null;
		String xmlPath = "/path/to/registry_xml"; // Needs to be changed
		
		MartRegistryFactory factory = new XmlMartRegistryFactory(xmlPath, null);
        Portal portal = new Portal(factory, null);

        Query query = new Query(portal);
        query.setProcessor("TSV");
        query.setClient("biomartclient");
        query.setLimit(-1);
        query.setHeader(true);

        Query.Dataset ds = query.addDataset("BioMart", "Human");
        ds.addFilter("human__chr_104", "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,MT,X,Y");
        ds.addFilter("human__strand_104", "-1,1");
        ds.addFilter("human__sorf_length_104", "10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100");
        ds.addFilter("human__annotation_104", "3UTR,5UTR,NMD,NSD,TEC,exonic,intergenic,intronic,lncrna,pseudogene,sORF");
        ds.addFilter("human__biotype_104", "TEC,antisense,lincRNA,non_stop_decay,nonsense_mediated_decay,processed_pseudogene,processed_transcript,protein_coding,retained_intron,sense_intronic,sense_overlapping,transcribed_processed_pseudogene,transcribed_unitary_pseudogene,transcribed_unprocessed_pseudogene,unitary_pseudogene,unprocessed_pseudogene");
        ds.addFilter("human__CELL_LINE_104", "andreev_2015,calviello_2016,crappe_2014,eichorn_2014,elkon_2015,fritsch_2012,gawron_2016,gonzalez_2014,grow_2015,guo_2010,ingolia_2012,ingolia_2014,iwasaki_2016,jan_2014,lee_2012,liu_2013,loayza_punch_2013,niu_2014,park_2016,rooijers_2013,rubio_2014,rutkowski_2015,sidrauski_2015,stern_ginossar_2012,stumpf_2013,tanenbaum_2015,tirosh_2015,wang_2015,wein_2014,werner_2015,wiita_2013,xu_2016,yoon_2014,zur_2016");
        ds.addFilter("human__start_codon_104", "AAG,ACG,AGG,ATA,ATC,ATG,ATT,CTG,GTG,TTG");
        ds.addAttribute("human__sorf_id_104");
        ds.addAttribute("human__chr_104");
        ds.addAttribute("human__sorf_begin_104");
        ds.addAttribute("human__strand_104");
        ds.addAttribute("human__sorf_end_104");
        ds.addAttribute("human__CELL_LINE_104");
        ds.addAttribute("human__spliced_104");
        ds.addAttribute("human__start_parts_104");
        ds.addAttribute("human__stop_parts_104");
        ds.addAttribute("human__sorf_length_104");
        ds.addAttribute("human__start_codon_104");
        ds.addAttribute("human__downstream_gene_distance_104");
        ds.addAttribute("human__upstream_gene_distance_104");
        ds.addAttribute("human__tr_seq_104");
        ds.addAttribute("human__aa_seq_104");
        ds.addAttribute("human__mass_104");
        ds.addAttribute("human__annotation_104");
        ds.addAttribute("human__biotype_104");
        ds.addAttribute("human__RPKM_104");
        ds.addAttribute("human__coverage_104");
        ds.addAttribute("human__coverage_uniformity_104");
        ds.addAttribute("human__exon_overlap_104");
        ds.addAttribute("human__in_frame_coverage_104");
        ds.addAttribute("human__in_frame_104");
        ds.addAttribute("human__pc_exon_overlap_104");
        ds.addAttribute("human__id_104");
        ds.addAttribute("human__Rltm_min_Rchx_104");
        ds.addAttribute("human__FLOSS_104");
        ds.addAttribute("human__classification_104");
        ds.addAttribute("human__orfscore_104");
        ds.addAttribute("human__peak_shift_104");
        ds.addAttribute("human__PhastCon_104");
        ds.addAttribute("human__PhyloP_104");
        ds.addAttribute("human__p_value_104");
        ds.addAttribute("variation__source_107");
        ds.addAttribute("variation__clinical_significance_107");
        ds.addAttribute("variation__description_107");
        ds.addAttribute("variation__name_107");
        ds.addAttribute("variation__seq_region_end_107");
        ds.addAttribute("variation__seq_region_start_107");
        ds.addAttribute("ReSpin__ID_106");
        ds.addAttribute("ReSpin__charge_106");
        ds.addAttribute("ReSpin__sequences_106");
        ds.addAttribute("ReSpin__file_106");
        ds.addAttribute("ReSpin__Rank_106");
        ds.addAttribute("ReSpin__mz_error_106");
        ds.addAttribute("ReSpin__precursormass_106");
        ds.addAttribute("ReSpin__fixed_mods_106");
        ds.addAttribute("ReSpin__variable_mods_106");
        ds.addAttribute("ReSpin__sorf_conf_106");

        // Print to System.out, but you can pass in any java.io.OutputStream
        query.getResults(System.out);

        System.exit(0);

		return results;
	}
	

}
